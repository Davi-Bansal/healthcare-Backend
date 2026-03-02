package com.curelex.healthcare.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam String path) {

        try {

            File file = new File(path);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            InputStreamResource resource =
                    new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("File error");
        }
    }
}
