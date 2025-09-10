/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.dto.request.ProviderRegisterRequestDTO;
import com.tba.enums.State;
import com.tba.enums.UserRole;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.services.CloudinaryService;
import com.tba.services.ServiceProviderService;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiProviderController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ServiceProviderService providerService;

    @PostMapping("/provider/register")
    public ResponseEntity<?> registerProvider(@Valid @ModelAttribute ProviderRegisterRequestDTO dto) {
        try {
            String imageUrl;
            try {
                imageUrl = cloudinaryService.uploadImage(dto.getAvatar(), "avatar").get("secure_url").toString();
            } catch (Exception ex) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi upload ảnh"));
            }

            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setAddress(dto.getAddress());
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setAvatar(imageUrl);
            user.setRole(UserRole.PROVIDER); // set role là PROVIDER
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            
            ServiceProvider provider = new ServiceProvider();
            provider.setUserId(user);
            provider.setCompanyName(dto.getCompanyName());
            provider.setIsApproved(false);
            provider.setState(State.PENDING);
            provider.setCreatedAt(new Date());
            provider.setUpdatedAt(new Date());

            this.providerService.addProvider(user, provider);
            System.out.println("✅ User ID sau khi lưu: " + user.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "id", user.getId(), // đảm bảo entity có ID sau khi save
                    "message", "Đăng ký nhà cung cấp thành công!"
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
             System.err.println("❌ Lỗi thực tế: " + ex.getClass() + " - " + ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }
    }
}
