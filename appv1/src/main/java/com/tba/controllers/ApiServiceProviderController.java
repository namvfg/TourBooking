/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.dto.request.ProviderRegisterRequestDTO;
import com.tba.dto.request.ServicePermissionRequestDTO;
import com.tba.dto.response.ServiceProviderResponseDTO;
import com.tba.enums.ServiceType;
import com.tba.enums.State;
import com.tba.enums.UserRole;
import com.tba.pojo.ServicePermission;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.services.CloudinaryService;
import com.tba.services.EmailService;
import com.tba.services.ServicePermissionService;
import com.tba.services.ServiceProviderService;
import com.tba.services.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiServiceProviderController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ServiceProviderService providerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServicePermissionService servicePermissionService;

    @Autowired
    private EmailService emailService;

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

            System.out.println("User ID sau khi lưu: " + user.getId());
            emailService.sendProviderPending(user.getEmail(), provider.getCompanyName());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "id", user.getId(),
                    "message", "Đăng ký nhà cung cấp thành công!"
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi thực tế: " + ex.getClass() + " - " + ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }
    }

    @GetMapping("secure/provider/profile")
    public ResponseEntity<ServiceProviderResponseDTO> getProviderProfile(Principal principal) {
        // Lấy user hiện tại
        User u = this.userService.getUserByUsername(principal.getName());

        // Lấy service provider gắn với user
        ServiceProvider sp = u.getServiceProvider();

        if (sp == null) {
            return ResponseEntity.notFound().build();
        }

        // Map sang DTO
        ServiceProviderResponseDTO res = new ServiceProviderResponseDTO(
                sp.getId(),
                sp.getCompanyName(),
                sp.getState(),
                sp.getCreatedAt(),
                sp.getUpdatedAt()
        );

        return ResponseEntity.ok(res);
    }

    @PostMapping("secure/provider/request-permission")
    public ResponseEntity<?> requestPermission(
            @RequestBody @Valid ServicePermissionRequestDTO request,
            Principal principal) {

        User u = this.userService.getUserByUsername(principal.getName());
        ServiceProvider provider = u.getServiceProvider();

        if (provider == null) {
            return ResponseEntity.badRequest().body("Tài khoản không phải là nhà cung cấp dịch vụ!");
        }

        Date now = new Date();
        List<ServicePermission> toSave = new ArrayList<>();
        List<ServiceType> skipped = new ArrayList<>();

        for (ServiceType type : request.getServiceTypes()) {
            boolean exists = this.servicePermissionService.existsByProviderAndServiceType(provider.getId(), type);
            if (exists) {
                skipped.add(type);
                continue;
            }

            ServicePermission sp = new ServicePermission();
            sp.setServiceType(type);
            sp.setState(State.PENDING);
            sp.setCreatedDate(now);
            sp.setUpdatedDate(now);
            sp.setServiceProviderId(provider);
            toSave.add(sp);
        }

        if (toSave.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "added", 0,
                    "skipped", skipped,
                    "message", "Tất cả các quyền đã được yêu cầu trước đó!"
            ));
        }

        this.servicePermissionService.addAllServicePermissions(toSave);

        String requestTypes = toSave.stream()
                .map(ServicePermission::getServiceType)
                .map(Enum::name)
                .reduce((a, b) -> a + ", " + b).orElse("");

        this.emailService.sendPermissionPending(
                u.getEmail(),
                provider.getCompanyName(),
                requestTypes
        );
        return ResponseEntity.ok(Map.of(
                "added", toSave.size(),
                "skipped", skipped,
                "message", "Yêu cầu xử lý thành công."
        ));
    }
}
