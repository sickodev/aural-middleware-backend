package com.glub.aural_middleware.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface AudioCompressor {
    File compressToOpus(MultipartFile multipartFile) throws IOException, InterruptedException;
}
