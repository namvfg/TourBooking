/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServiceProvider;
import com.tba.repositories.ServiceProviderRepository;
import com.tba.services.ServiceProviderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tba.pojo.ServiceProvider;
import com.tba.services.ServiceProviderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @GetMapping("/providers")
    public String listProviders(Model model) {
        List<ServiceProvider> providers = serviceProviderService.getAllServiceProviders();
        model.addAttribute("providers", providers);
        return "table_provider";
    }

    @GetMapping("/provider/delete/{id}")
    public String deleteProvider(@PathVariable("id") Integer id) {
        serviceProviderService.deleteProvider(id);
        return "redirect:/providers";
    }
}
