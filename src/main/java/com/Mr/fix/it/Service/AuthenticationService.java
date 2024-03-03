package com.Mr.fix.it.Service;

import com.Mr.fix.it.DTO.*;
import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.validation.BindingResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;

import jakarta.mail.MessagingException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.DisabledException;

import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.UniqueException;

import com.Mr.fix.it.Util.Helper;

import com.Mr.fix.it.Entity.*;
import com.Mr.fix.it.Entity.Enum.Gender;
import com.Mr.fix.it.Entity.Enum.UserType;

import com.Mr.fix.it.Repository.*;

import com.Mr.fix.it.Config.Security.JwtService;

import com.Mr.fix.it.Request.FcmRequest;
import com.Mr.fix.it.Request.UserRegisterRequest;
import com.Mr.fix.it.Request.WorkerRegisterRequest;
import com.Mr.fix.it.Request.AuthenticationRequest;

import com.Mr.fix.it.Response.TokensResponse;
import com.Mr.fix.it.Response.CategoriesResponse;
import com.Mr.fix.it.Response.GenericResponse;
import com.Mr.fix.it.Response.AuthenticationResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService
{
    private final UserRepository userRepository;

    private final WorkerRepository workerRepository;

    private final FavoriteRepository favoriteRepository;

    private final CategoryRepository categoryRepository;

    private final RegisterTokenRepository registerTokenRepository;

    private final WorkingLocationRepository workingLocationRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final ImageUploadingService imageUploadingService;

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Value(value = "${token.expiration.time}")
    private long userTokenLifeTime;

    @Value(value = "${refresh.token.expiration.time}")
    private long userRefreshTokenLifeTime;

    public GenericResponse registerClient(
        UserRegisterRequest request,
        BindingResult result
    ) throws
        ValidationException,
        UniqueException,
        IOException
    {
        return registerUser(request, result, UserType.CLIENT);
    }

    public GenericResponse registerWorker(
        WorkerRegisterRequest request,
        BindingResult result
    ) throws
        ValidationException,
        UniqueException,
        IOException
    {
        return registerUser(request, result, UserType.WORKER);
    }

    public AuthenticationResponse authenticate(
        AuthenticationRequest request,
        BindingResult result
    ) throws
        ValidationException,
            NotFoundException
    {
        Helper.fieldsValidate(result);

        var user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("user not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new NotFoundException("user not found");

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var token = jwtService.generateToken(user, userTokenLifeTime);
        var refreshToken = jwtService.generateToken(user, userRefreshTokenLifeTime);
        TokensResponse tokens = buildTokensResponse(token, refreshToken);

        return buildAuthenticationResponse(user, tokens);
    }

    @Transactional
    public boolean verifyUser(String token)
    {
        Optional<RegisterToken> registerToken = registerTokenRepository.findByToken(token);

        if(registerToken.isEmpty())
            return false;

        Optional<User> user = userRepository.findById(registerToken.get().getUser().getId());

        if(user.isEmpty())
            return false;

        if(LocalDateTime.now().isAfter(registerToken.get().getExpiryDate()) && !user.get().getIsVerified())
        {
            registerTokenRepository.deleteById(registerToken.get().getId());

            if(user.get().getType() == UserType.WORKER)
            {
                Optional<Worker> worker = (workerRepository.findByUserId(user.get().getId()));

                if(worker.isPresent())
                {
                    workingLocationRepository.deleteAllByWorkerId(worker.get().getId());
                    workerRepository.deleteById(worker.get().getId());
                }
            }

            userRepository.deleteById(user.get().getId());
            return false;
        }

        user.get().setIsVerified(true);
        userRepository.save(user.get());
        registerTokenRepository.deleteById(registerToken.get().getId());

        return true;
    }

    public GenericResponse forgetPasswordRequest(String email) throws NotFoundException
    {
        var user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        if(!user.getIsVerified() || !user.getIsActive())
            throw new DisabledException("user is disabled");

        var token = jwtService.generateToken(user, userTokenLifeTime);
        sendVerificationOrResetEmail(
            user.getEmail(), "Reset your password!", "Reset Password",
            "To reset your password, please click the button below:",
            "If you did not request to reset password, you can safely ignore this email.",
            "Reset Password",
            "http://13.60.3.70/api/auth/reset-password-form",
            token
        );

        return GenericResponse.builder()
            .state("success")
            .message("a password recovery message have been sent to your email")
            .build();
    }

    @Transactional
    public boolean changeUserPassword(String token, String password)
    {
        if(checkResetLinkExpiration(token))
            return false;

        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        var user = userRepository.findByEmail(email);

        if(user.isEmpty())
            return false;

        if(!user.get().getIsVerified() || !user.get().getIsActive())
            return false;

        user.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(user.get());

        return true;
    }

    public boolean checkResetLinkExpiration(String token)
    {
        try
        {
            return jwtService.extractExpiration(token).before(Calendar.getInstance().getTime());
        }
        catch(ExpiredJwtException e)
        {
            return true;
        }
    }

    public TokensResponse refreshToken(String refreshToken)
    {
        String email = jwtService.extractUsername(refreshToken);

        var user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        if(!user.getIsVerified() || !user.getIsActive())
            throw new DisabledException("user is disabled");

        var token = jwtService.generateToken(user, userTokenLifeTime);

        if(userRefreshTokenLifeTime - jwtService.extractExpiration(refreshToken).getTime() < 86400000)
            refreshToken = jwtService.generateToken(user, userRefreshTokenLifeTime);

        return TokensResponse
            .builder()
            .token(token)
            .refreshToken(refreshToken)
            .build();
    }

    public CategoriesResponse getCategories()
    {
        List<CategoryDTO> categoryDTOS = categoryRepository
            .findAll()
            .stream()
            .map(category ->
                CategoryDTO
                    .builder()
                    .type(category.getType())
                    .build()
            )
            .toList();

        return CategoriesResponse
            .builder()
            .categories(categoryDTOS)
            .build();
    }

    private GenericResponse registerUser(
        UserRegisterRequest request,
        BindingResult result,
        UserType userType
    ) throws
        ValidationException,
        UniqueException,
        IOException
    {
        Helper.fieldsValidate(result);
        uniqueValidate(request);

        var user = buildUser(request, userType);

        if (userType == UserType.CLIENT)
        {
            userRepository.save(user);
        }
        else if (userType == UserType.WORKER)
        {
            WorkerRegisterRequest workerRequest = (WorkerRegisterRequest) request;
            var worker = buildWorker(workerRequest, user);
            workerRepository.save(worker);
        }

        var jwtToken = jwtService.generateToken(user, userTokenLifeTime);
        var registerToken = buildRegisterToken(user, jwtToken);
        registerTokenRepository.save(registerToken);

        sendVerificationOrResetEmail(
            user.getEmail(), "Verify your Account!", "Welcome to Mr.fix it!",
            "Thank you for signing up. To activate your account, please click the button below:",
            "If you did not sign up for this account, you can safely ignore this email.",
            "Activate Now",
            "http://13.60.3.70/api/auth/verify",
            jwtToken
        );

        String message = userType == UserType.CLIENT ? "user registered, check email to verify account" : "worker registered, check email to verify account";

        return GenericResponse
            .builder()
            .state("success")
            .message(message)
            .build();
    }

    private void uniqueValidate(UserRegisterRequest request) throws UniqueException
    {
        if(userRepository.findByEmail(request.getEmail()).isPresent())
            throw new UniqueException("email already exists");

        if(userRepository.findByPhone(request.getPhone()).isPresent())
            throw new UniqueException("phone already exists");
    }

    private User buildUser(
        UserRegisterRequest request,
        UserType userType
    ) throws IOException
    {
        LocalDate dob = Helper.getLocalDate(request.getDob());

        String fileName = Helper.generateFileName(request.getProfilePicture());
        File file = imageUploadingService.convertToFile(request.getProfilePicture(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        return User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dob(dob)
            .gender(Gender.valueOf(request.getGender().toUpperCase()))
            .city(request.getCity())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phone(request.getPhone())
            .img(uri)
            .type(userType)
            .fcm(null)
            .isActive(true)
            .isVerified(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    private Worker buildWorker(WorkerRegisterRequest workerRequest, User user)
    {
        var category = categoryRepository.findByType(workerRequest.getCategory());

        var worker = Worker.builder()
            .user(user)
            .category(category.orElse(null))
            .rate(0f)
            .build();

        List<WorkingLocation> workingLocations = getWorkingLocations(workerRequest, worker);
        worker.setWorkingLocations(workingLocations);

        return worker;
    }

    private List<WorkingLocation> getWorkingLocations(WorkerRegisterRequest workerRequest, Worker worker)
    {
        return workerRequest
            .getWorkingLocations()
            .stream()
            .map((location) ->
                WorkingLocation
                    .builder()
                    .worker(worker)
                    .locality(location.getLocality())
                    .latitude(location.getParsedLatitude())
                    .longitude(location.getParsedLongitude())
                    .build()
            )
            .toList();
    }

    private RegisterToken buildRegisterToken(User user, String jwtToken)
    {
        return RegisterToken
            .builder()
            .user(user)
            .token(jwtToken)
            .expiryDate(LocalDateTime.now().plus(userTokenLifeTime, ChronoUnit.MILLIS))
            .build();
    }

    private void sendVerificationOrResetEmail(
        String to, String subject,String header,String starting,
        String ending, String buttonText, String link,
        String token
    )
    {
        new Thread(() ->
        {
            try
            {
                emailService.sendVerificationOrResetPasswordEmail(
                    to, subject, header,
                    starting, ending, buttonText,
                    link,token
                );
            }
            catch (MessagingException e)
            {
                logger.error("An exception occurred: ", e);
            }
        }).start();
    }

    private TokensResponse buildTokensResponse(String token, String refreshToken)
    {
        return TokensResponse
            .builder()
            .token(token)
            .refreshToken(refreshToken)
            .build();
    }

    private AuthenticationResponse buildAuthenticationResponse(User user, TokensResponse tokens)
    {
        if(user.getType() == UserType.WORKER)
        {
            var worker = workerRepository.findByUserId(user.getId())
                .orElseThrow(()-> new NotFoundException("worker not found"));

            return AuthenticationResponse
                .builder()
                .user(getWorkerDto(worker))
                .tokens(tokens)
                .build();
        }
        else
        {
            return AuthenticationResponse
                .builder()
                .user(getUserDto(user))
                .tokens(tokens)
                .build();
        }
    }

    private UserDTO getUserDto(User user)
    {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(user.getId());
        List<WorkerDTO> favoriteDTOS = favorites
            .stream()
            .map(favorite -> getWorkerDto(favorite.getWorker()))
            .toList();

        return UserDTO
            .builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .dob(user.getDob())
            .gender(user.getGender().name())
            .city(user.getCity())
            .email(user.getEmail())
            .phone(user.getPhone())
            .img(user.getImg())
            .type(user.getType())
            .favorites(favoriteDTOS)
            .build();
    }

    private WorkerDTO getWorkerDto(Worker worker)
    {
        return WorkerDTO
            .builder()
            .id(worker.getUser().getId())
            .img(worker.getUser().getImg())
            .type(worker.getUser().getType())
            .phone(worker.getUser().getPhone())
            .email(worker.getUser().getEmail())
            .dob(worker.getUser().getDob())
            .city(worker.getUser().getCity())
            .gender(worker.getUser().getGender().name())
            .lastName(worker.getUser().getLastName())
            .firstName(worker.getUser().getFirstName())
            .createdAt(worker.getUser().getCreatedAt())
            .workerID(worker.getId())
            .enabled(worker.getUser().getIsActive())
            .rate(worker.getRate())
            .favorites(new ArrayList<>())
            .category(CategoryDTO.builder().id(worker.getCategory().getId()).type(worker.getCategory().getType()).totalWorkers(workerRepository.findWorkersByCategoryType(worker.getCategory().getType()).size()).build())
            .ads(worker.getAds().stream().map(ad -> new AdsDTO(ad.getId(), ad.getWorker().getId(), ad.getWorker().getUser().getFirstName(), ad.getWorker().getUser().getLastName(), ad.getPosterImg(), ad.getStartDate(), ad.getExpiryDate())).toList())
            .previousWorks(worker.getPreviousWorks().stream().map(previousWork -> new PreviousWorkDTO(previousWork.getId(), previousWork.getDescription(), previousWork.getPreviousWorkImgs().stream().map(img -> ImageDTO.builder().img(img.getImg()).build()).toList())).toList())
            .workingLocations(worker.getWorkingLocations().stream().map(workingLocation -> new WorkingLocationDTO(workingLocation.getId(), workingLocation.getLocality(), workingLocation.getLatitude(), workingLocation.getLongitude())).toList())
            .build();
    }

    @Transactional
    public GenericResponse updateFCM(
        FcmRequest request,
        BindingResult result,
        String token
    ) throws
        ValidationException,
        NotFoundException
    {
        Helper.fieldsValidate(result);

        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        user.setFcm(request.getFcm());
        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message("fcm updated successfully")
            .build();
    }

    public GenericResponse logout(
        String token
    ) throws NotFoundException
    {
        String email = jwtService.extractUsername(token);

        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user not found"));

        user.setFcm(null);
        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message("logged out successfully")
            .build();
    }
}