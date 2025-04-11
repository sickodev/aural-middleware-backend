package com.glub.aural_middleware.service.impl;

import com.glub.aural_middleware.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        byte[] fileBytes = file.getBytes();

        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, fileName);

        ResponseEntity<String> response = webClient.put()
                .uri(uploadUrl)
                .header("apikey", apiKey)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(fileBytes)
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response.getStatusCode().is2xxSuccessful()) {
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        } else {
            throw new RuntimeException("Failed to upload to Supabase: " + response.getStatusCode() + " " + response.getBody());
        }
    }
}
