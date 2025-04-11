package com.glub.aural_middleware.controller;

import com.glub.aural_middleware.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            String fileUrl = fileUploadService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }
}
