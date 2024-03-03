package com.Mr.fix.it.Util;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.List;
import java.util.UUID;
import java.util.Objects;
import java.time.LocalDate;

import com.Mr.fix.it.Exception.ExceptionType.ValidationException;

public class Helper
{
    private Helper()
    {
    }

    public static void fieldsValidate(BindingResult result) throws ValidationException
    {
        if (result.hasErrors())
        {
            List<String> errorMessages = result
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

            throw new ValidationException(errorMessages.toString());
        }
    }

    public static LocalDate getLocalDate(String date)
    {
        return LocalDate.of(
            Integer.parseInt(date.split("-")[0]),
            Integer.parseInt(date.split("-")[1]),
            Integer.parseInt(date.split("-")[2])
        );
    }

    public static String generateFileName(MultipartFile file)
    {
        return UUID.randomUUID() + "-" + StringUtils.cleanPath(
            Objects.requireNonNull(
                file.getOriginalFilename()
            )
        );
    }

    public static String getFileUri(String filename, String directory)
    {
//        return ServletUriComponentsBuilder
//            .fromCurrentContextPath()
//            .path("/"+directory+"/")
//            .path(filename)
//            .toUriString();
        return directory + filename;
    }
}
