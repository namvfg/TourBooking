package com.tba.controllers;

import com.tba.dto.response.ServicePostResponseDTO;
import com.tba.pojo.ServicePost;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.enums.ServiceType;
import com.tba.enums.UserRole;
import com.tba.services.ServicePostService;
import com.tba.services.ServiceProviderService;
import com.tba.services.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
public class ApiServicePostController {

    @Autowired
    private ServicePostService servicePostService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    // API: Lấy danh sách giá trị enum ServiceType cho frontend dropdown
    @GetMapping("/service-types")
    public ResponseEntity<List<String>> getServiceTypes() {
        List<String> types = Arrays.stream(ServiceType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    // USER & PROVIDER: Lấy danh sách bài đăng, hỗ trợ phân trang
    @GetMapping(value = "/service-post/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllServicePostsPaged(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        List<ServicePost> posts = servicePostService.getServicePostsPaged(page, size);
        List<ServicePostResponseDTO> result = posts.stream().map(this::toResponseDTO).collect(Collectors.toList());
        long total = servicePostService.countServicePosts();
        int totalPages = (int) Math.ceil((double) total / size);
        return ResponseEntity.ok(
                Map.of(
                        "data", result,
                        "page", page,
                        "size", size,
                        "total", total,
                        "totalPages", totalPages
                )
        ); // KHÔNG trả về kiểu String!
    }

    // USER & PROVIDER: Xem chi tiết một bài đăng
    @GetMapping("/service-post/{id}")
    public ResponseEntity<ServicePostResponseDTO> getServicePostById(@PathVariable("id") Integer id) {
        ServicePost post = servicePostService.getServicePostById(id);
        if (post == null || post.getIsDeleted()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponseDTO(post));
    }

    // PROVIDER: Thêm bài đăng mới (nhận multipart/form-data, file ảnh)
    @PostMapping("/secure/service-post/add")
    public ResponseEntity<?> addServicePost(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image,
            @RequestParam("price") String price, // nhận price kiểu String
            @RequestParam("availableSlot") Integer availableSlot,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceProviderId") Integer serviceProviderId,
            Principal principal) {

        User user = userService.getUserByUsername(principal.getName());
        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body("Chỉ nhà cung cấp mới được đăng bài.");
        }

        ServiceProvider provider = user.getServiceProvider();
        if (provider == null || !provider.getId().equals(serviceProviderId)) {
            return ResponseEntity.status(403).body("Bạn không có quyền đăng bài cho provider này.");
        }

        // UPLOAD ẢNH LÊN CLOUDINARY
        String imageUrl = null;
        try {
            Map result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            imageUrl = (String) result.get("secure_url");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi upload ảnh dịch vụ!");
        }

        ServicePost post = new ServicePost();
        post.setName(name);
        post.setDescription(description);
        post.setImage(imageUrl);
        post.setPrice(new java.math.BigDecimal(price)); // chuyển sang BigDecimal
        post.setAvailableSlot(availableSlot);
        post.setAddress(address);
        try {
            post.setServiceType(ServiceType.valueOf(serviceType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Loại dịch vụ không hợp lệ!");
        }
        post.setCreatedDate(new Date());
        post.setUpdatedDate(new Date());
        post.setIsDeleted(false);
        post.setServiceProviderId(provider);

        servicePostService.addServicePost(post);
        return ResponseEntity.ok(toResponseDTO(post));
    }

    // PROVIDER: Sửa bài đăng
    @PutMapping("/secure/service-post/edit/{id}")
    public ResponseEntity<?> updateServicePost(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("price") String price,
            @RequestParam("availableSlot") Integer availableSlot,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            Principal principal) {
        System.out.println("PRINCIPAL: " + (principal != null ? principal.getName() : "null"));
        User user = userService.getUserByUsername(principal.getName());
        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body("Chỉ nhà cung cấp mới được sửa bài.");
        }

        ServicePost post = servicePostService.getServicePostById(id);
        if (post == null || post.getIsDeleted()) {
            return ResponseEntity.notFound().build();
        }

        ServiceProvider provider = user.getServiceProvider();
        if (provider == null || !provider.getId().equals(post.getServiceProviderId().getId())) {
            return ResponseEntity.status(403).body("Bạn chỉ được sửa bài của chính mình.");
        }

        post.setName(name);
        post.setDescription(description);
        // Nếu có ảnh mới, upload lên Cloudinary
        if (image != null && !image.isEmpty()) {
            try {
                Map result = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) result.get("secure_url");
                post.setImage(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi upload ảnh dịch vụ!");
            }
        }
        post.setPrice(new java.math.BigDecimal(price));
        post.setAvailableSlot(availableSlot);
        post.setAddress(address);
        try {
            post.setServiceType(ServiceType.valueOf(serviceType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Loại dịch vụ không hợp lệ!");
        }
        post.setUpdatedDate(new Date());

        servicePostService.updateServicePost(post);
        return ResponseEntity.ok(toResponseDTO(post));
    }

    // PROVIDER: Xóa mềm bài đăng
    @DeleteMapping("/secure/service-post/delete/{id}")
    public ResponseEntity<?> deleteServicePost(@PathVariable("id") Integer id, Principal principal) {
        System.out.println("PRINCIPAL: " + (principal != null ? principal.getName() : "null"));
        User user = userService.getUserByUsername(principal.getName());
        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body("Chỉ nhà cung cấp mới được xóa bài.");
        }

        ServicePost post = servicePostService.getServicePostById(id);
        if (post == null || post.getIsDeleted()) {
            return ResponseEntity.notFound().build();
        }

        ServiceProvider provider = user.getServiceProvider();
        if (provider == null || !provider.getId().equals(post.getServiceProviderId().getId())) {
            return ResponseEntity.status(403).body("Bạn chỉ được xóa bài của chính mình.");
        }

        servicePostService.softDeleteServicePost(id);
        return ResponseEntity.ok().body("Xóa bài đăng thành công!");
    }

    // Hàm chuyển đổi entity sang response DTO
    private ServicePostResponseDTO toResponseDTO(ServicePost post) {
        ServicePostResponseDTO dto = new ServicePostResponseDTO();
        dto.setId(post.getId());
        dto.setName(post.getName());
        dto.setDescription(post.getDescription());
        dto.setImage(post.getImage());
        dto.setPrice(post.getPrice());
        dto.setAvailableSlot(post.getAvailableSlot());
        dto.setAddress(post.getAddress());
        dto.setServiceType(post.getServiceType().name());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setUpdatedDate(post.getUpdatedDate());
        dto.setIsDeleted(post.getIsDeleted());
        dto.setServiceProviderId(post.getServiceProviderId().getId());
        dto.setCompanyName(post.getServiceProviderId().getCompanyName());
        return dto;
    }
}
