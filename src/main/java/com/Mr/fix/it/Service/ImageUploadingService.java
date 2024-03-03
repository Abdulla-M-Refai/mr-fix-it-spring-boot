package com.Mr.fix.it.Service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class ImageUploadingService
{
    public File convertToFile(MultipartFile multipartFile, String fileName) throws IOException
    {
        File tempFile = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile))
        {
            fos.write(multipartFile.getBytes());
            fos.close();
        }

        return tempFile;
    }

    public String uploadFile(File file, String fileName) throws IOException
    {
        BlobId blobId = BlobId.of("mrfixit-8e013.appspot.com", fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        InputStream inputStream = ImageUploadingService.class.getClassLoader().getResourceAsStream("mrfixit-8e013-firebase-adminsdk-uz6h1-852a9fba74.json");
        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        file.delete();
        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/mrfixit-8e013.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }
}
