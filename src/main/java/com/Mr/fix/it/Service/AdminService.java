package com.Mr.fix.it.Service;

import com.Mr.fix.it.Config.Security.JwtService;
import com.Mr.fix.it.DTO.*;
import com.Mr.fix.it.Entity.*;
import com.Mr.fix.it.Entity.Enum.Gender;
import com.Mr.fix.it.Entity.Enum.TaskStatus;
import com.Mr.fix.it.Entity.Enum.UserType;
import com.Mr.fix.it.Exception.ExceptionType.NotAuthorizedException;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;
import com.Mr.fix.it.Exception.ExceptionType.UniqueException;
import com.Mr.fix.it.Exception.ExceptionType.ValidationException;
import com.Mr.fix.it.Repository.*;
import com.Mr.fix.it.Request.*;
import com.Mr.fix.it.Response.*;
import com.Mr.fix.it.Util.Helper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService
{
    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final WorkerRepository workerRepository;

    private final DonationRepository donationRepository;

    private final TaskRepository taskRepository;

    private final AdsRepository adsRepository;

    private final FeaturedRepository featuredRepository;

    private final EmailService emailService;

    private final FavoriteRepository favoriteRepository;

    private final CategoryRepository categoryRepository;

    private final WorkingLocationRepository workingLocationRepository;

    private final ImageUploadingService imageUploadingService;

    @Value(value = "${token.expiration.time}")
    private long userTokenLifeTime;

    @Value(value = "${refresh.token.expiration.time}")
    private long userRefreshTokenLifeTime;

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

        if(user.getType() != UserType.ADMIN)
            throw new NotAuthorizedException("unauthorized user");

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var token = jwtService.generateToken(user, userTokenLifeTime);
        var refreshToken = jwtService.generateToken(user, userRefreshTokenLifeTime);
        TokensResponse tokens = buildTokensResponse(token, refreshToken);

        return AuthenticationResponse
            .builder()
            .user(getUserDto(user))
            .tokens(tokens)
            .build();
    }

    private TokensResponse buildTokensResponse(String token, String refreshToken)
    {
        return TokensResponse
            .builder()
            .token(token)
            .refreshToken(refreshToken)
            .build();
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
            .enabled(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public StatisticsResponse getStatistics()
    {
        List<User> users = userRepository.findAll();
        List<User> clients = users.stream().filter(user -> user.getType() == UserType.CLIENT).toList();
        List<User> workers = users.stream().filter(user -> user.getType() == UserType.WORKER).toList();

        int totalClients = clients.size();
        int totalWorkers = workers.size();

        int clientsPercentage = (int)(((float)totalClients / users.size()) * 100);
        int workersPercentage = (int)(((float)totalWorkers / users.size()) * 100);

        List<String> clientsCategories = clients.stream().map(user -> user.getCreatedAt().toString().substring(0,user.getCreatedAt().toString().indexOf("T"))).toList().stream().distinct().toList();
        List<String> workersCategories = workers.stream().map(user -> user.getCreatedAt().toString().substring(0,user.getCreatedAt().toString().indexOf("T"))).toList().stream().distinct().toList();

        List<Integer> clientsValues = new ArrayList<>();
        List<Integer> workersValues = new ArrayList<>();

        clientsCategories = clientsCategories.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        workersCategories = workersCategories.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        for(String value : clientsCategories)
            clientsValues.add(clients.stream().filter(user -> user.getCreatedAt().toString().substring(0,user.getCreatedAt().toString().indexOf("T")).equals(value)).toList().size());

        for(String value : workersCategories)
            workersValues.add(workers.stream().filter(user -> user.getCreatedAt().toString().substring(0,user.getCreatedAt().toString().indexOf("T")).equals(value)).toList().size());

        List<Ads> ads = adsRepository.findAll();

        int totalAds = ads.size();
        int adsRevenue = totalAds * 5;

        List<Featured> featureds = featuredRepository.findAll();

        int totalFeatured = featureds.size();
        int featuredRevenue = totalFeatured * 15;

        List<Donation> donations = donationRepository.findAll();
        int donationRevenue = 0;

        for (Donation donation : donations)
            donationRevenue += donation.getAmount();

        int totalRevenue = adsRevenue + featuredRevenue + donationRevenue;

        int adsRevenuePercentage = (int)(((float)adsRevenue / totalRevenue) * 100);
        int featuredRevenuePercentage = (int)(((float)featuredRevenue / totalRevenue) * 100);
        int donationRevenuePercentage = (int)(((float)donationRevenue / totalRevenue) * 100);

        List<String> adsRevenueCategories = ads.stream().map(ad -> ad.getStartDate().toString().substring(0,ad.getStartDate().toString().indexOf("T"))).toList().stream().distinct().toList();
        List<String> featuredRevenueCategories = featureds.stream().map(featured -> featured.getStartDate().toString().substring(0,featured.getStartDate().toString().indexOf("T"))).toList().stream().distinct().toList();
        List<String> donationRevenueCategories = donations.stream().map(donation -> donation.getDonationDate().toString().substring(0,donation.getDonationDate().toString().indexOf("T"))).toList().stream().distinct().toList();

        List<Integer> adsRevenueValues = new ArrayList<>();
        List<Integer> featuredRevenueValues = new ArrayList<>();
        List<Integer> donationRevenueValues = new ArrayList<>();

        adsRevenueCategories = adsRevenueCategories.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        featuredRevenueCategories = featuredRevenueCategories.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        donationRevenueCategories = donationRevenueCategories.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        for(String value : adsRevenueCategories)
            adsRevenueValues.add(ads.stream().filter(ad -> ad.getStartDate().toString().substring(0,ad.getStartDate().toString().indexOf("T")).equals(value)).toList().size() * 5);

        for(String value : featuredRevenueCategories)
            featuredRevenueValues.add(featureds.stream().filter(featured -> featured.getStartDate().toString().substring(0,featured.getStartDate().toString().indexOf("T")).equals(value)).toList().size() * 15);

        for(String value : donationRevenueCategories)
            donationRevenueValues.add(donations.stream().filter(donation -> donation.getDonationDate().toString().substring(0,donation.getDonationDate().toString().indexOf("T")).equals(value)).toList().stream().mapToInt(Donation::getAmount).sum());

        return StatisticsResponse
            .builder()
            .clients(totalClients)
            .clientsPercentage(clientsPercentage)
            .clientsValues(clientsValues)
            .clientsCategories(clientsCategories)
            .workers(totalWorkers)
            .workersPercentage(workersPercentage)
            .workersValues(workersValues)
            .workersCategories(workersCategories)
            .adsRevenue(adsRevenue)
            .adsRevenuePercentage(adsRevenuePercentage)
            .adsRevenueValues(adsRevenueValues)
            .adsRevenueCategories(adsRevenueCategories)
            .featuredRevenue(featuredRevenue)
            .featuredRevenuePercentage(featuredRevenuePercentage)
            .featuredRevenueValues(featuredRevenueValues)
            .featuredRevenueCategories(featuredRevenueCategories)
            .donationRevenue(donationRevenue)
            .donationRevenuePercentage(donationRevenuePercentage)
            .donationRevenueValues(donationRevenueValues)
            .donationRevenueCategories(donationRevenueCategories)
            .build();
    }

    public ClientsResponse getClients()
    {
        List<User> users = userRepository.findAll();
        users = users.stream().filter(user -> user.getType() == UserType.CLIENT).toList();

        List<UserDTO> userDTOS = users.stream().map(this::getUserDto).toList();
        return ClientsResponse.builder().users(userDTOS).build();
    }

    @Transactional
    public GenericResponse deleteClient(
        long id
    ) throws NotFoundException
    {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("user not found"));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        return GenericResponse
            .builder()
            .state("success")
            .message("user deleted successfully")
            .build();
    }

    @Transactional
    public ClientResponse updateUser(
        Long id,
        UserUpdateRequest userUpdateRequest,
        BindingResult result
    ) throws
        ValidationException,
        UniqueException
    {
        Helper.fieldsValidate(result);

        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("user not found"));

        String email = user.getEmail();
        String phone = user.getPhone();

        if(
            userRepository.findByEmail(userUpdateRequest.getEmail()).isPresent() &&
                !email.equalsIgnoreCase(userUpdateRequest.getEmail())
        ) throw new UniqueException("email already exists");

        if(
            userRepository.findByPhone(userUpdateRequest.getPhone()).isPresent() &&
                !phone.equalsIgnoreCase(userUpdateRequest.getPhone())
        ) throw new UniqueException("phone already exists");

        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setDob(Helper.getLocalDate(userUpdateRequest.getDob()));
        user.setGender(Gender.valueOf(userUpdateRequest.getGender().toUpperCase()));
        user.setCity(userUpdateRequest.getCity());
        user.setEmail(userUpdateRequest.getEmail());
        user.setPhone(userUpdateRequest.getPhone());

        userRepository.save(user);

        return ClientResponse
            .builder()
            .user(getUserDto(user))
            .build();
    }

    public ClientResponse createClient(AdminCreateClientRequest request, BindingResult result)
    {
        Helper.fieldsValidate(result);

        uniqueValidate(request);
        var user = buildUser(request);

        String password = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        userRepository.flush();

        if(user.getType() == UserType.WORKER)
        {
            System.out.println(request.getCategory());
            var worker = Worker
                .builder()
                .rate(0f)
                .user(user)
                .category(categoryRepository.findByType(request.getCategory()).get())
                .build();

            workerRepository.save(worker);
        }

        new Thread(() ->
        {
            try
            {
                emailService.sendEmail(
                        user.getEmail(),
                        "Your Account Created Successfully",
                        "<h1>Welcome To MR.FIX IT!</h1> your password is: " + password
                );
            }
            catch (MessagingException e)
            {
                throw new RuntimeException(e);
            }
        }).start();

        return ClientResponse.builder().user(getUserDto(userRepository.findByEmail(user.getEmail()).get())).build();
    }

    private void uniqueValidate(AdminCreateClientRequest request) throws UniqueException
    {
        if(userRepository.findByEmail(request.getEmail()).isPresent())
            throw new UniqueException("email already exists");

        if(userRepository.findByPhone(request.getPhone()).isPresent())
            throw new UniqueException("phone already exists");
    }

    private User buildUser(AdminCreateClientRequest request)
    {
        LocalDate dob = Helper.getLocalDate(request.getDob());
        String defaultImg = Helper.getFileUri("default-user.png", "images/");

        return User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dob(dob)
            .gender(Gender.valueOf(request.getGender().toUpperCase()))
            .city(request.getCity())
            .email(request.getEmail())
            .password(null)
            .phone(request.getPhone())
            .img(defaultImg)
            .type(UserType.valueOf(request.getType()))
            .fcm(null)
            .isActive(true)
            .isVerified(true)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static String generateRandomPassword() {
        String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";
        String NUMBERS = "0123456789";
        String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
        String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length())));
        password.append(UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length())));

        for (int i = 4; i < 10; i++) {
            int category = random.nextInt(3); // 0 for numbers, 1 for lowercase, 2 for uppercase
            switch (category) {
                case 0:
                    password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
                    break;
                case 1:
                    password.append(LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length())));
                    break;
                case 2:
                    password.append(UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length())));
                    break;
            }
        }

        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }

        return new String(passwordArray);
    }

    public WorkersResponse getWorkers()
    {
        List<Worker> workers = workerRepository.findAll();
        List<WorkerDTO> workersDTO = workers.stream().map(this::getWorkerDto).toList();

        return WorkersResponse
            .builder()
            .workers(workersDTO)
            .build();
    }

    public WorkingLocationResponse addWorkingLocation(
        Long id,
        WorkingLocationRequest request,
        BindingResult result
    ) throws
        ValidationException,
        NotFoundException
    {
        Helper.fieldsValidate(result);

        var worker = workerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("worker not found"));

        var workingLocation = WorkingLocation
            .builder()
            .worker(worker)
            .locality(request.getLocality())
            .latitude(request.getParsedLatitude())
            .longitude(request.getParsedLongitude())
            .build();

        workingLocationRepository.save(workingLocation);
        workingLocationRepository.flush();

        var workingLocationDTO = WorkingLocationDTO
            .builder()
            .id(workingLocation.getId())
            .locality(workingLocation.getLocality())
            .latitude(workingLocation.getLatitude())
            .longitude(workingLocation.getLongitude())
            .build();

        return WorkingLocationResponse
            .builder()
            .workingLocation(workingLocationDTO)
            .build();
    }

    @Transactional
    public GenericResponse deleteWorkingLocation(
        long id
    ) throws NotFoundException
    {
        var workingLocation = workingLocationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("working location not found"));

        var worker = workingLocation.getWorker();

        worker
            .getWorkingLocations()
            .set(worker.getWorkingLocations().indexOf(workingLocation), null);

        workingLocationRepository.delete(workingLocation);

        return GenericResponse
            .builder()
            .state("success")
            .message("working location deleted successfully")
            .build();
    }

    public ClientsResponse getNewComers()
    {
        List<User> users = userRepository.findNewcomersWorkers();
        List<UserDTO> userDTOS = users.stream().map(this::getUserDto).toList();
        return ClientsResponse.builder().users(userDTOS).build();
    }

    public TasksResponse getTasks()
    {
        List<Task> tasks = taskRepository.findAll();

        List<TaskDTO> userTasksDTOList = tasks.stream()
            .map(task -> TaskDTO.builder()
                .id(task.getId())
                .client(getUserDto(task.getUser()))
                .worker(task.getWorker() != null ? getWorkerDto(task.getWorker()) : null)
                .category(CategoryDTO.builder().type(task.getCategory().getType()).build())
                .locality(task.getLocality())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .title(task.getTitle())
                .description(task.getDescription())
                .price(task.getPrice() != null ? task.getPrice() : -1)
                .startDate(task.getStartDate())
                .expiryDate(task.getExpiryDate())
                .type(task.getType())
                .status(task.getStatus())
                .taskImgs(task.getTaskImgs().stream()
                    .map(taskImg -> ImageDTO.builder().img(taskImg.getImg()).build())
                    .toList())
                .offers(task.getOffers().stream()
                    .map(offer -> OfferDTO.builder()
                        .id(offer.getId())
                        .taskID(offer.getTask().getId())
                        .worker(offer.getWorker() != null ? getWorkerDto(offer.getWorker()) : null)
                        .price(offer.getPrice())
                        .build())
                    .toList())
                .feedback(task.getFeedback())
                .build())
            .toList();

        return TasksResponse
            .builder()
            .tasks(userTasksDTOList)
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
    public TaskResponse updateTaskStatus(long id, UpdateStatusRequest request, BindingResult result) throws NotFoundException
    {
        Helper.fieldsValidate(result);

        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("task not found"));

        task.setStatus(TaskStatus.valueOf(request.getStatus()));
        taskRepository.save(task);

        TaskDTO taskDTO = TaskDTO.builder()
                .id(task.getId())
                .client(getUserDto(task.getUser()))
                .worker(task.getWorker() != null ? getWorkerDto(task.getWorker()) : null)
                .category(CategoryDTO.builder().type(task.getCategory().getType()).build())
                .locality(task.getLocality())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .title(task.getTitle())
                .description(task.getDescription())
                .price(task.getPrice() != null ? task.getPrice() : -1)
                .startDate(task.getStartDate())
                .expiryDate(task.getExpiryDate())
                .type(task.getType())
                .status(task.getStatus())
                .taskImgs(task.getTaskImgs().stream()
                        .map(taskImg -> ImageDTO.builder().img(taskImg.getImg()).build())
                        .toList())
                .offers(task.getOffers().stream()
                        .map(offer -> OfferDTO.builder()
                                .id(offer.getId())
                                .taskID(offer.getTask().getId())
                                .worker(offer.getWorker() != null ? getWorkerDto(offer.getWorker()) : null)
                                .price(offer.getPrice())
                                .build())
                        .toList())
                .feedback(task.getFeedback())
                .build();

        return TaskResponse.builder().task(taskDTO).build();
    }

    @Transactional
    public GenericResponse deleteTask(long id)
    {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("task not found"));

        taskRepository.delete(task);

        return GenericResponse
            .builder()
            .state("success")
            .message("task deleted successfully")
            .build();
    }

    public CategoriesResponse getCategories()
    {
        List<Worker> workers = workerRepository.findAll();

        List<CategoryDTO> categoryDTOS = categoryRepository
            .findAll()
            .stream()
            .map(category ->
                CategoryDTO
                    .builder()
                    .id(category.getId())
                    .type(category.getType())
                    .totalWorkers(workers.stream().filter(worker -> worker.getCategory()!=null && worker.getCategory().getType().equals(category.getType())).toList().size())
                    .build()
            )
            .toList();

        return CategoriesResponse
            .builder()
            .categories(categoryDTOS)
            .build();
    }

    public CategoryResponse createCategory(CreateCategoryRequest request, BindingResult result)
    {
        System.out.println(request.getCategory());
        Helper.fieldsValidate(result);

        if(categoryRepository.findByType(request.getCategory()).isPresent())
            throw new UniqueException("duplicated category");

        Category category = Category.builder().type(request.getCategory()).build();
        categoryRepository.save(category);
        categoryRepository.flush();

        CategoryDTO categoryDTO = CategoryDTO
            .builder()
            .id(category.getId())
            .type(category.getType())
            .totalWorkers(0)
            .build();

        return CategoryResponse.builder().category(categoryDTO).build();
    }

    @Transactional
    public GenericResponse deleteCategory(long id)
    {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("category not found"));

        List<Worker> workers = workerRepository.findAll();

        workers.forEach(worker -> {
            if(worker.getCategory() != null && worker.getCategory().getType().equals(category.getType()))
                worker.setCategory(null);
        });

        workerRepository.saveAll(workers);

        List<Task> tasks = taskRepository.findAll();

        tasks.forEach(task -> {
            if(task.getCategory() != null && task.getCategory().getType().equals(category.getType()))
                task.setCategory(null);
        });

        taskRepository.saveAll(tasks);

        categoryRepository.delete(category);

        return GenericResponse
            .builder()
            .state("success")
            .message("category deleted successfully")
            .build();
    }

    public AdsResponse getAds()
    {
        List<Ads> ads = adsRepository.findAll();
        return buildAdsDtoListFromAds(ads);
    }

    private AdsResponse buildAdsDtoListFromAds(List<Ads> ads) {
        List<AdsDTO> adsDtoList = ads
            .stream()
            .map(ad -> new AdsDTO(ad.getId(), ad.getWorker().getId(), ad.getWorker().getUser().getFirstName(), ad.getWorker().getUser().getLastName(), ad.getPosterImg(), ad.getStartDate(), ad.getExpiryDate()))
            .toList();

        return AdsResponse
            .builder()
            .ads(adsDtoList)
            .build();
    }

    public AdResponse createAd(
        long id,
        ImageRequest request,
        BindingResult result
    ) throws
        NotFoundException,
        ValidationException,
        IOException
    {
        Helper.fieldsValidate(result);

        var worker = workerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("worker not found"));

        String fileName = Helper.generateFileName(request.getImg());
        File file = imageUploadingService.convertToFile(request.getImg(), fileName);
        String uri = imageUploadingService.uploadFile(file, fileName);

        var ad = Ads
            .builder()
            .posterImg(uri)
            .worker(worker)
            .startDate(LocalDateTime.now())
            .expiryDate(LocalDateTime.now().plusDays(30))
            .build();

        adsRepository.save(ad);
        adsRepository.flush();

        var adDTO = AdsDTO
            .builder()
            .id(ad.getId())
            .poster(ad.getPosterImg())
            .startDate(ad.getStartDate())
            .expiryDate(ad.getExpiryDate())
            .build();

        return AdResponse
            .builder()
            .ad(adDTO)
            .build();
    }

    @Transactional
    public GenericResponse deleteAd(long id) throws NotFoundException
    {
        Ads ad = adsRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("ad not found"));

        ad.getWorker().getAds().set(ad.getWorker().getAds().indexOf(ad),null);
        adsRepository.delete(ad);

        return GenericResponse
                .builder().state("success").message("ad deleted successfully").build();
    }

    public FeaturedsResponse getFeaturedWorkers()
    {
        List<Featured> featureds = featuredRepository.findAll();

        List<FeaturedWorkerDTO> featuredWorkerDTOS = featureds
            .stream()
            .map(
                f -> FeaturedWorkerDTO
                    .builder()
                    .id(f.getId())
                    .worker(getWorkerDto(f.getWorker()))
                    .startDate(f.getStartDate())
                    .expiryDate(f.getExpiryDate())
                    .build()
            ).toList();

        return FeaturedsResponse
                .builder()
                .featureds(featuredWorkerDTOS)
                .build();
    }

    public FeaturedWorkerResponse createFeatured(long id) throws NotFoundException
    {
        var worker = workerRepository.findById(id)
            .orElseThrow(() ->  new NotFoundException("worker not found"));

        Featured featured = featuredRepository.findByWorkerID(worker.getId())
            .or(() ->
                Optional.ofNullable(
                    Featured
                        .builder()
                        .worker(worker)
                        .startDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .build()
                )
            ).get();

        featuredRepository.save(featured);

        FeaturedWorkerDTO featuredWorkerDTO = FeaturedWorkerDTO
                .builder()
                .id(featured.getId())
                .worker(getWorkerDto(featured.getWorker()))
                .startDate(featured.getStartDate())
                .expiryDate(featured.getExpiryDate())
                .build();

        return FeaturedWorkerResponse.builder().featured(featuredWorkerDTO).build();
    }

    @Transactional
    public GenericResponse deleteFeatured(long id) throws NotFoundException
    {
        Featured featured = featuredRepository.findById(id)
            .orElseThrow(() ->  new NotFoundException("featured worker not found"));

        featuredRepository.delete(featured);

        return GenericResponse
                .builder().state("success").message("featured worker deleted successfully").build();
    }

    public WorkersResponse getNotFWorkers()
    {
        List<Featured> featureds = featuredRepository.findAll();
        List<Worker> workers = workerRepository.findAll();

        Set<Long> featuredIds = featureds.stream()
            .map(Featured::getWorker)
            .map(Worker::getId)
            .collect(Collectors.toSet());

        List<Worker> nonFeaturedWorkers = workers.stream()
            .filter(worker -> !featuredIds.contains(worker.getId()))
            .toList();

        List<WorkerDTO> workersDTO = nonFeaturedWorkers.stream().map(this::getWorkerDto).toList();

        return WorkersResponse
            .builder()
            .workers(workersDTO)
            .build();
    }

    public DonationsResponse getDonations()
    {
        List<Donation> donations = donationRepository.findAll();

        List<DonationDTO> donationDTOS = donations
                .stream()
                .map(donation ->
                    DonationDTO
                        .builder()
                        .id(donation.getId())
                        .donationDate(donation.getDonationDate())
                        .amount(donation.getAmount())
                        .user(getUserDto(donation.getUser()))
                        .build()
                )
                .toList();

        return DonationsResponse.builder().donations(donationDTOS).build();
    }
}
