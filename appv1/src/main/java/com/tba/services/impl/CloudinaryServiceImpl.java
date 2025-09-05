/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tba.services.CloudinaryService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File rỗng");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload hình ảnh");
        }

        Map res = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "image",
                        "folder", folder,
                        "use_filename", true,
                        "unique_filename", true,
                        "overwrite", true
                )
        );
        if (res == null) {
            throw new IllegalStateException("Không nhận được info từ Cloudinary");
        }
        return res;
    }

    @Override
    public boolean deleteImageByPublicId(String publicId) throws Exception {
        if (publicId == null || publicId.isBlank()) {
            return true;
        }

        Map res = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("invalidate", true, "resource_type", "image")
        );
        Object result = res.get("result");
        return result != null && ("ok".equals(result) || "not found".equals(result));
    }

    @Override
    public String extractPublicIdFromUrl(String secureUrl) {
        if (secureUrl == null || secureUrl.isBlank()) {
            return null;
        }

        int uploadIdx = secureUrl.indexOf("/upload/");
        if (uploadIdx < 0) {
            return null;
        }

        String afterUpload = secureUrl.substring(uploadIdx + "/upload/".length());
        String[] parts = afterUpload.split("/");

        int start = (parts.length > 0 && parts[0].startsWith("v") && parts[0].length() > 1
                && Character.isDigit(parts[0].charAt(1))) ? 1 : 0;

        StringBuilder sb = new StringBuilder();
        for (int i = start; i < parts.length; i++) {
            if (i > start) {
                sb.append('/');
            }
            sb.append(parts[i]);
        }

        String last = sb.toString();
        int dot = last.lastIndexOf('.');
        if (dot > 0) {
            last = last.substring(0, dot);
        }
        return last;
    }
}
