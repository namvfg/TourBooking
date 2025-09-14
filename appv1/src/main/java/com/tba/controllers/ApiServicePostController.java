package com.tba.controllers;

import com.tba.dto.response.ServicePostResponseDTO;
import com.tba.pojo.ServicePost;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.pojo.Room;
import com.tba.pojo.Tour;
import com.tba.pojo.Transportation;
import com.tba.enums.ServiceType;
import com.tba.enums.UserRole;
import com.tba.services.ServicePostService;
import com.tba.services.ServiceProviderService;
import com.tba.services.UserService;
import com.tba.services.RoomService;
import com.tba.services.TourService;
import com.tba.services.TransportationService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tba.enums.State;
import com.tba.pojo.ServicePermission;
import com.tba.services.CloudinaryService;
import com.tba.services.ServicePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
    private ServicePermissionService permissionService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private TourService tourService;

    @Autowired
    private TransportationService transportationService;

    @Autowired
    private CloudinaryService cloudinaryService;


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
        );
    }


@GetMapping("/service-post/search")
public ResponseEntity<?> searchServicePosts(
    @RequestParam Map<String, String> params,
    @RequestParam(name = "page", defaultValue = "0") Integer page,
    @RequestParam(name = "size", defaultValue = "10") Integer size
) {
    params.put("page", String.valueOf(page));
    params.put("size", String.valueOf(size));

    List<ServicePost> posts = servicePostService.getServicePosts(params);
    List<ServicePostResponseDTO> result = posts.stream()
        .map(this::toResponseDTO)
        .collect(Collectors.toList());

    // Lấy tổng bản ghi đúng filter
    Map<String, String> filterParams = new HashMap<>(params);
    filterParams.remove("page");
    filterParams.remove("size");
    long total = servicePostService.countServicePostsWithFilters(filterParams);
    int totalPages = (int) Math.ceil((double) total / size);

    return ResponseEntity.ok(
        Map.of(
            "data", result,
            "page", page,
            "size", size,
            "total", total,
            "totalPages", totalPages
        )
    );
}
    @GetMapping("/service-post/{id}")
    public ResponseEntity<ServicePostResponseDTO> getServicePostById(@PathVariable("id") Integer id) {
        ServicePost post = servicePostService.getServicePostById(id);
        if (post == null || post.getIsDeleted()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponseDTO(post));
    }

    @PostMapping(value = "/secure/service-post/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addServicePost(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image,
            @RequestParam("price") String price,
            @RequestParam("availableSlot") Integer availableSlot,
            @RequestParam("address") String address,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("serviceProviderId") Integer serviceProviderId,
            @RequestParam(value = "roomStartDate", required = false) String roomStartDate,
            @RequestParam(value = "roomEndDate", required = false) String roomEndDate,
            @RequestParam(value = "tourStartDate", required = false) String tourStartDate,
            @RequestParam(value = "tourEndDate", required = false) String tourEndDate,
            @RequestParam(value = "transportType", required = false) String transportType,
            @RequestParam(value = "transportStartDate", required = false) String transportStartDate,
            @RequestParam(value = "destination", required = false) String destination,
            Principal principal) {

        User user = userService.getUserByUsername(principal.getName());
        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body("Chỉ nhà cung cấp mới được đăng bài.");
        }

        ServiceProvider provider = user.getServiceProvider();
        if (provider == null || !provider.getId().equals(serviceProviderId)) {
            return ResponseEntity.status(403).body("Bạn không có quyền đăng bài cho provider này.");
        }

        String imageUrl;
        try {
           imageUrl = cloudinaryService.uploadImage(image, "service-post")
                        .get("secure_url").toString();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi upload ảnh dịch vụ!");
        }

        ServicePost post = new ServicePost();
        post.setName(name);
        post.setDescription(description);
        post.setImage(imageUrl);
        post.setPrice(new java.math.BigDecimal(price));
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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date roomStart = null, roomEnd = null, tourStart = null, tourEnd = null, transportStart = null;
        try {
            if (roomStartDate != null && !roomStartDate.isEmpty())
                roomStart = sdf.parse(roomStartDate);
            if (roomEndDate != null && !roomEndDate.isEmpty())
                roomEnd = sdf.parse(roomEndDate);
            if (tourStartDate != null && !tourStartDate.isEmpty())
                tourStart = sdf.parse(tourStartDate);
            if (tourEndDate != null && !tourEndDate.isEmpty())
                tourEnd = sdf.parse(tourEndDate);
            if (transportStartDate != null && !transportStartDate.isEmpty())
                transportStart = sdf.parse(transportStartDate);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Ngày giờ không đúng định dạng! Định dạng phải là yyyy-MM-dd'T'HH:mm");
        }

        // Sau khi thêm ServicePost, thêm bản ghi chi tiết theo loại dịch vụ
       
            switch (post.getServiceType().name()) {
                case "ROOM":
                    Room room = new Room();
                    room.setStartDate(roomStart);
                    room.setEndDate(roomEnd);
                    roomService.addRoom(post,room);
                    break;
                case "TOUR":
                    Tour tour = new Tour();
                    tour.setStartDate(tourStart);
                    tour.setEndDate(tourEnd);
                    tourService.addTour(post,tour);
                    break;
                case "TRANSPORTATION":
                    Transportation transportation = new Transportation();
                    transportation.setTransportType(transportType);
                    transportation.setStartDate(transportStart);
                    transportation.setDestination(destination);
                    transportationService.addTransportation(post,transportation);
                    break;
                default:
                    break;
            }
        

        return ResponseEntity.ok(toResponseDTO(post));
    }

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
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(image, "service-post")
                        .get("secure_url").toString();
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

    @DeleteMapping("/secure/service-post/delete/{id}")
    public ResponseEntity<?> deleteServicePost(@PathVariable("id") Integer id, Principal principal) {
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

        // Thêm thông tin chi tiết nếu có
        if ("ROOM".equals(dto.getServiceType()) && post.getRoom() != null) {
            dto.setRoomStartDate(post.getRoom().getStartDate());
            dto.setRoomEndDate(post.getRoom().getEndDate());
        } else if ("TOUR".equals(dto.getServiceType()) && post.getTour() != null) {
            dto.setTourStartDate(post.getTour().getStartDate());
            dto.setTourEndDate(post.getTour().getEndDate());
        } else if ("TRANSPORTATION".equals(dto.getServiceType()) && post.getTransportation() != null) {
            dto.setTransportType(post.getTransportation().getTransportType());
            dto.setTransportStartDate(post.getTransportation().getStartDate());
            dto.setDestination(post.getTransportation().getDestination());
        }
        return dto;
    }
    
    @GetMapping("/secure/provider/service-permissions")
    public ResponseEntity<List<String>> getActiveServiceTypesForProvider(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null || user.getRole() != UserRole.PROVIDER) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }
        ServiceProvider provider = user.getServiceProvider();
        if (provider == null) {
            return ResponseEntity.status(403).body(Collections.emptyList());
        }
        List<ServicePermission> perms = permissionService.getPermissionsByProviderId(provider.getId());
        List<String> allowedTypes = perms.stream()
            .filter(p -> p.getState() == State.ACTIVE)
            .map(p -> p.getServiceType().name())
            .collect(Collectors.toList());
        return ResponseEntity.ok(allowedTypes);
    }
}