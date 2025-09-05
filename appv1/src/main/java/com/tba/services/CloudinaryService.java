/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface CloudinaryService {

    Map<String, Object> uploadImage(MultipartFile file, String folder) throws Exception;

    boolean deleteImageByPublicId(String publicId) throws Exception;

    String extractPublicIdFromUrl(String secureUrl);
}
