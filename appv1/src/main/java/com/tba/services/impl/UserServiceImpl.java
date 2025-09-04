/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tba.dto.response.UserResponseDTO;
import com.tba.pojo.User;
import com.tba.repositories.UserRepository;
import com.tba.services.UserService;
import jakarta.validation.ValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private void validateParams(Map<String, String> params) {
        // Validate username
        String username = params.get("username");
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new ValidationException("Username must be between 3 and 20 characters");
        }

        // Validate password
        String password = params.get("password");
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }

        // Validate fullname
        String fullname = params.get("fullname");
        if (fullname == null || fullname.trim().isEmpty()) {
            throw new ValidationException("Fullname cannot be empty");
        }

        // Validate email
        String email = params.get("email");
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            throw new ValidationException("Email must be valid");
        }

        // Validate phone number
        String phoneNumber = params.get("phoneNumber");
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new ValidationException("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException("Phone number must be 10 digits");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepository.getUserByUsername(username);
    }

    @Override
    public UserResponseDTO addUser(Map<String, String> params, MultipartFile avatar) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addUserWithFormBinding(User user) {
        if (this.userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại. Vui lòng chọn tên khác.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!user.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(user.getFile().getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            user.setAvatar("https://res.cloudinary.com/dxxwcby8l/image/upload/v1692330009/vuyk886cdgjykoi6qs3f.png");
        }
        this.userRepository.addUserWithFormBinding(user);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepository.authenticate(username, password);
    }

    @Override
    public int getUserIdByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username không được rỗng");
        }
        return this.userRepository.getUserIdByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username");
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(u.getRole())));
        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

}
