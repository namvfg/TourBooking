/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.dto.request.ProviderRatingRequestDTO;
import com.tba.dto.request.ProviderRegisterRequestDTO;
import com.tba.dto.request.ProviderUpdateRequestDTO;
import com.tba.dto.request.ServicePermissionRequestDTO;
import com.tba.dto.response.ProviderRatingResponseDTO;
import com.tba.dto.response.ServiceProviderResponseDTO;
import com.tba.enums.ServiceType;
import com.tba.enums.State;
import com.tba.enums.UserRole;
import com.tba.pojo.ProviderRating;
import com.tba.pojo.ServicePermission;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.services.CloudinaryService;
import com.tba.services.EmailService;
import com.tba.services.ProviderRatingService;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private ProviderRatingService providerRatingService;

    @PostMapping("/provider/register")
    public ResponseEntity<?> registerProvider(@Valid @ModelAttribute ProviderRegisterRequestDTO dto) {
        try {
            if (userService.existsByUsername(dto.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Tên đăng nhập đã tồn tại!"
                ));
            }
            if (userService.existsByEmail(dto.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Email đã được sử dụng!"
                ));
            }
            if (userService.existsByPhoneNumber(dto.getPhoneNumber())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Số điện thoại đã được sử dụng!"
                ));
            }

            if (providerService.existsByCompanyName(dto.getCompanyName())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Tên công ty đã tồn tại trong hệ thống!"
                ));
            }

            String imageUrl;
            try {
                imageUrl = cloudinaryService.uploadImage(dto.getAvatar(), "avatar")
                        .get("secure_url").toString();
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
            user.setRole(UserRole.PROVIDER);
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
    public ResponseEntity<?> getProviderProfile(Principal principal) {

        User u = this.userService.getUserByUsername(principal.getName());

        ServiceProvider sp = u.getServiceProvider();

        if (sp == null) {
            return ResponseEntity.badRequest().body("Tài khoản không phải là nhà cung cấp dịch vụ!");
        }
        if (sp.getState() == State.PENDING) {
            return ResponseEntity.badRequest().body("Tài khoản đang chờ duyệt!");
        }

        if (sp.getState() == State.DISABLED) {
            return ResponseEntity.badRequest().body("Tài khoản đã bị khóa!");
        }

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

        if (provider.getState() == State.PENDING) {
            return ResponseEntity.badRequest().body("Tài khoản đang chờ duyệt!");
        }

        if (provider.getState() == State.DISABLED) {
            return ResponseEntity.badRequest().body("Tài khoản đã bị khóa!");
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

    @GetMapping("secure/provider/check-status")
    public ResponseEntity<?> checkProviderStatus(Principal principal) {
        User u = this.userService.getUserByUsername(principal.getName());


        if (u == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy người dùng!"));
        }


        if (u.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.ok(Map.of("status", "ACTIVE", "message", "Bạn đăng nhập bình thường!"));
        }

        ServiceProvider sp = u.getServiceProvider();
        if (sp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin nhà cung cấp"));
        }

        switch (sp.getState()) {
            case ACTIVE:
                return ResponseEntity.ok(Map.of("status", "ACTIVE", "message", "Bạn đã được duyệt. Hãy vào trang chủ!"));
            case DISABLED:
                return ResponseEntity.ok(Map.of("status", "DISABLED", "message", "Tài khoản của bạn đã bị khóa!"));
            case PENDING:
                return ResponseEntity.ok(Map.of("status", "PENDING", "message", "Tài khoản của bạn đang chờ duyệt!"));
            default:
                return ResponseEntity.ok(Map.of("status", "UNKNOWN", "message", "Trạng thái không xác định!"));
        }
    }

    @GetMapping("/provider/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable("id") Integer id) {
        ServiceProvider sp = providerService.getProviderById(id);
        if (sp == null) {
            return ResponseEntity.notFound().build();
        }

        User user = sp.getUserId();

        ServiceProviderResponseDTO res = new ServiceProviderResponseDTO(
                sp.getId(),
                sp.getCompanyName(),
                sp.getState(),
                sp.getCreatedAt(),
                sp.getUpdatedAt(),
                user.getAddress(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAvatar()
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping("/secure/provider/{id}/rating")
    public ResponseEntity<?> getProviderRatings(@PathVariable("id") Integer providerId) {
        List<ProviderRating> ratings = providerRatingService.getRatingsByProviderId(providerId);
        List<ProviderRatingResponseDTO> dtos = ratings.stream().map(r -> {
            User user = r.getUserId();
            return new ProviderRatingResponseDTO(
                    r.getId(),
                    user.getId(),
                    user.getFirstName() + " " + user.getLastName(),
                    user.getAvatar(),
                    r.getRate(),
                    r.getComment(),
                    r.getCreatedDate()
            );
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/secure/provider/{id}/rating")
    public ResponseEntity<?> addOrUpdateRating(@PathVariable("id") Integer providerId,
            @RequestBody ProviderRatingRequestDTO dto,
            Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("Bạn cần đăng nhập!");
        }
        ServiceProvider provider = providerService.getProviderById(providerId);
        if (provider == null) {
            return ResponseEntity.badRequest().body("Nhà cung cấp không tồn tại!");
        }

        ProviderRating rating = providerRatingService.findByUserAndProvider(user.getId(), providerId);
        if (rating == null) {
            providerRatingService.addRating(user, provider, dto.getRate(), dto.getComment());
        } else {
            rating.setRate((short) dto.getRate());
            rating.setComment(dto.getComment());
            providerRatingService.updateRating(rating);
        }
        return ResponseEntity.ok("Đã gửi đánh giá");
    }

    @PostMapping("/secure/provider/update-profile")
    public ResponseEntity<?> updateProviderProfile(
            @Valid @ModelAttribute ProviderUpdateRequestDTO dto,
            Principal principal) {

        String username = principal.getName();
        User user = userService.getUserByUsername(username);

        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền thực hiện hành động này!"));
        }

        ServiceProvider provider = user.getServiceProvider();
        if (provider == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Không tìm thấy thông tin nhà cung cấp"));
        }


        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }


        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(dto.getAvatar(), "avatar")
                        .get("secure_url").toString();
                user.setAvatar(imageUrl);
            } catch (Exception ex) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Lỗi upload ảnh: " + ex.getMessage()));
            }
        }


        if (dto.getCompanyName() != null) {
            provider.setCompanyName(dto.getCompanyName());
        }

        user.setUpdatedAt(new Date());
        provider.setUpdatedAt(new Date());

        userService.updateUser(user);
        providerService.updateProvider(provider);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cập nhật thông tin nhà cung cấp thành công!"
        ));
    }
}
