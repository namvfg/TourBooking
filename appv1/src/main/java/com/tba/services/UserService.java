/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services;

import com.tba.dto.response.UserResponseDTO;
import com.tba.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public interface UserService extends UserDetailsService {

    User getUserByUsername(String username);

    UserResponseDTO addUser(Map<String, String> params, MultipartFile avatar);

    void addUserWithFormBinding(User user);

    boolean authenticate(String username, String password);

    int getUserIdByUsername(String username);
    
    List<User> getAllUsers();
}
