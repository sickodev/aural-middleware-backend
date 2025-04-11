package com.glub.aural_middleware.service.impl;

import com.glub.aural_middleware.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final String UPLOADED_DIR = "uploads/";

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        try {
            Path uploadPath = Paths.get(UPLOADED_DIR + file.getOriginalFilename());

            Files.createDirectories(uploadPath.getParent());

            file.transferTo(uploadPath);

            return uploadPath.toString();
        }catch (Exception e) {
            throw new Exception("File upload failed: " + e.getMessage());
        }
    }
}
