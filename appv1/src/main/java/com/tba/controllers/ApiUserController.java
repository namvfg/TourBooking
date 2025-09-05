/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.dto.request.LoginRequestDTO;
import com.tba.dto.request.UserRegisterRequestDTO;
import com.tba.dto.response.UserResponseDTO;
import com.tba.enums.UserRole;
import com.tba.pojo.User;
import com.tba.services.CloudinaryService;
import com.tba.services.UserService;
import com.tba.utils.JwtUtils;
import jakarta.validation.ValidationException;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
@PropertySource("classpath:configs.properties")
public class ApiUserController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private Environment env;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        if (!userService.authenticate(dto.getUsername(), dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Sai thông tin đăng nhập"));
        }
        try {
            User user = userService.getUserByUsername(dto.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Sai thông tin đăng nhập"));
            }

            String role = user.getRole().toString();
            String token = JwtUtils.generateToken(user.getUsername(), role);

            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Lỗi khi tạo JWT"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute UserRegisterRequestDTO dto) {
        try {
            String phonePattern = this.env.getProperty("PHONE_PATTERN");
            String emailPattern = this.env.getProperty("EMAIL_PATTERN");
            String passwordPattern = this.env.getProperty("PASSWORD_PATTERN");

            if (dto.getFirstName().isBlank()
                    || dto.getLastName().isBlank() || dto.getEmail().isBlank()
                    || dto.getAddress().isBlank() || dto.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thông tin không hợp lệ"));
            }

            if (userService.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email đã tồn tại"));
            }

            if (userService.existsByPhoneNumber(dto.getPhoneNumber())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Số điện thoại đã tồn tại"));
            }

            if (userService.existsByUsername(dto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username đã tồn tại"));
            }

            if (dto.getUsername().length() < 3 || dto.getUsername().length() > 20) {
                throw new ValidationException("Username phải dài từ 3 đến 20 ký tự");
            }

            if (!dto.getEmail().matches(emailPattern)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email không hợp lệ"));
            }

            if (!dto.getPhoneNumber().matches(phonePattern)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại không hợp lệ"));
            }

            if (!dto.getPassword().matches(passwordPattern)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu phải từ 6 ký tự và chứa cả chữ lẫn số"));
            }

            String imageUrl;
            try {
                imageUrl = cloudinaryService.uploadImage(dto.getAvatar(), "avatar").get("secure_url").toString();
            } catch (Exception ex) {
                return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi up ảnh"));
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
            user.setRole(UserRole.USER); // default role
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            boolean success = userService.addUser(user);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Đăng ký thành công " + dto.getFirstName() + " " + dto.getLastName()));
            } else {
                return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi trong quá trình lưu người dùng"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }

    }

    @GetMapping("/secure/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Principal principal) {
        User u = this.userService.getUserByUsername(principal.getName());

        UserResponseDTO res = new UserResponseDTO(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getUsername(),
                u.getAvatar(), 
                u.getRole(),
                u.getAddress(),
                u.getPhoneNumber(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );

        return ResponseEntity.ok(res);
    }

}
