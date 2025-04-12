package com.glub.aural_middleware.service.impl;

import com.glub.aural_middleware.service.AudioCompressor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class AudioCompressorImpl  implements AudioCompressor {
    @Override
    public File compressToOpus(MultipartFile multipartFile) throws IOException, InterruptedException {
        // 1. Save original file to temp
        File originalFile = File.createTempFile("original-", ".wav"); // or .mp3 depending on source
        FileOutputStream fos = new FileOutputStream(originalFile);
        fos.write(multipartFile.getBytes());
        fos.close();

        // 2. Create output file path
        File compressedFile = File.createTempFile("compressed-", ".opus");

        // 3. Compress using FFmpeg
        String[] command = {
                "ffmpeg", "-y",
                "-i", originalFile.getAbsolutePath(),
                "-c:a", "libopus",
                "-b:a", "24k",
                compressedFile.getAbsolutePath()
        };

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg compression failed");
        }

        return compressedFile;
    }
}
