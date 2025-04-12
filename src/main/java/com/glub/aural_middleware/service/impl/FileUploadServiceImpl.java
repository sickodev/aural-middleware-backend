package com.glub.aural_middleware.service.impl;

import com.glub.aural_middleware.service.AudioCompressor;
import com.glub.aural_middleware.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.nio.file.Files;
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
    private final AudioCompressor audioCompressor;

    public FileUploadServiceImpl(AudioCompressor audioCompressor) {
        this.webClient = WebClient.builder().build();
        this.audioCompressor = audioCompressor;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // Step 1: Compress the audio to .opus using the AudioCompressor service
            File compressedFile = audioCompressor.compressToOpus(file);
            byte[] fileBytes = Files.readAllBytes(compressedFile.toPath());

            // Step 2: Generate a safe, random filename for the compressed file
            String originalName = compressedFile.getName(); // This will be the name of the compressed file
            String extension = StringUtils.getFilenameExtension(originalName);
            String randomId = UUID.randomUUID().toString();
            String fileName = randomId + (extension != null ? "." + extension : ".opus"); // Ensure .opus extension

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
