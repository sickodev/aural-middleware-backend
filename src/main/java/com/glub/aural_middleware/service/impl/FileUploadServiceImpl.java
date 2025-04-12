package com.glub.aural_middleware.service.impl;

import com.glub.aural_middleware.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.apikey}")
    private String apiKey;

    private final WebClient webClient;

    public FileUploadServiceImpl() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // Step 1: Generate safe, random filename
            String originalName = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalName);
            String randomId = UUID.randomUUID().toString();
            String fileName = randomId + (extension != null ? "." + extension : "");

            // Step 2: Read bytes
            byte[] fileBytes = file.getBytes();

            // Step 3: Upload to Supabase
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, fileName);

            ResponseEntity<String> response = webClient.post()
                    .uri(uploadUrl)
                    .header("apikey", apiKey)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .headers(headers -> headers.setContentLength(fileBytes.length))
                    .bodyValue(fileBytes)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            // Step 4: Success check
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
            } else {
                throw new RuntimeException("File upload failed: " +
                        (response != null ? response.getStatusCode() : "No response from Supabase"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during file upload: " + e.getMessage(), e);
        }
    }

}
