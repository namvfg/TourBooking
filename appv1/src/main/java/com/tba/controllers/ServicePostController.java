/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServicePost;
import com.tba.services.ServicePostService;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ServicePostController {

    @Autowired
    private ServicePostService servicePostService;

    @GetMapping("/services")
    public String listServices(Model model) {
        List<ServicePost> services = servicePostService.getServicePosts(new HashMap<>());
        model.addAttribute("services", services);
        return "table_service";
    }

    @GetMapping("/service/delete/{id}")
    public String softDeleteServicePost(@PathVariable("id") Integer id) {
        servicePostService.softDeleteServicePost(id);
        return "redirect:/services";
    }
}
