/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.enums.UserRole;
import com.tba.pojo.User;
import com.tba.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Admin
 */
@Controller
public class UserController {

    @Autowired
    private UserService UserService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/user")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        return "user";
    }

    @PostMapping("/user")
    public String createUser(Model model, @Valid @ModelAttribute("user") User user) {
        try {
            model.addAttribute("showModalFlag", true);
            model.addAttribute("roles", UserRole.values());
            this.UserService.addUserWithFormBinding(user);
            model.addAttribute("successMessage", "Tạo tài khoản thành công");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "username đã tồn tại");
        } catch (Exception e) {
            System.out.println("Lỗi thêm user: " + e);
        }
        return null;
    }

    @GetMapping("/users")
    public String list(Model model) {
        List<User> users = UserService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("active", "users");
        return "table_user";
    }
    
    
    
}
