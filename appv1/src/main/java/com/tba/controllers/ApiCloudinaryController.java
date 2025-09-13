/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.services.CloudinaryService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiCloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @PostMapping("/secure/upload-image")
    public ResponseEntity<?> uploadImageForCkeditor(@RequestParam("upload") MultipartFile file) {
        try {
            Map<String, Object> result = cloudinaryService.uploadImage(file, "ckeditor");

            return ResponseEntity.ok(Map.of(
                "uploaded", 1,
                "fileName", result.get("original_filename"),
                "url", result.get("secure_url")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "uploaded", 0,
                "error", Map.of("message", e.getMessage())
            ));
        }
    }
}
