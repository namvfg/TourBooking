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

    User addUser(User u);

    void addUserWithFormBinding(User user);

    boolean authenticate(String username, String password);

    int getUserIdByUsername(String username);
    
    List<User> getAllUsers();
}
