/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories;

import com.tba.pojo.User;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface UserRepository {

    User getUserByUsername(String username);

    boolean existsByUsername(String username);

    void addUser(User user);

    void addUserWithFormBinding(User user);

    boolean authenticate(String username, String password);

    int getUserIdByUsername(String username);
    
    List<User> getAllUsers();
    
    void deleteUser(Integer id);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    User getUserById(Integer id);
    
    User getUserByProviderId(Integer providerId);
    
    long countByRoleAndMonthYear(String role, Integer month, Integer year);
    
    void updateUser(User user);
}
