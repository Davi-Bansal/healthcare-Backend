package com.curelex.healthcare.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

public class FileUploadUtil {

    // ✅ allowed extensions
    private static final Set<String> ALLOWED =
            Set.of("jpg","jpeg","png","pdf");

    // ✅ max size = 5MB
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public static String saveFile(String folder, MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new Exception("File is empty");
        }

        // ✅ size validation
        if (file.getSize() > MAX_SIZE) {
            throw new Exception("File too large. Max 5MB allowed");
        }

        // ✅ extension validation
        String original = file.getOriginalFilename();

        if (original == null || !original.contains(".")) {
            throw new Exception("Invalid file name");
        }

        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();

        if (!ALLOWED.contains(ext)) {
            throw new Exception("Only JPG, PNG, PDF allowed");
        }

        // ✅ create folder if not exists
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        // ✅ unique name
        String newName = UUID.randomUUID()+"."+ext;

        Path path = Paths.get(folder, newName);

        Files.copy(file.getInputStream(), path);

        return path.toString();
    }
}
