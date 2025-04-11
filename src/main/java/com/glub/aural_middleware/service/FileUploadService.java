package com.glub.aural_middleware.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String uploadFile(MultipartFile file) throws Exception;
}
