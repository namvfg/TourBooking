package com.tba.controllers;

import com.tba.enums.State;
import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.services.ServiceProviderService;
import com.tba.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/provider/update/{id}")
    public String showUpdateProvider(@PathVariable("id") Integer id, Model model) {
        ServiceProvider provider = serviceProviderService.getProviderById(id);
        model.addAttribute("provider", provider);
        model.addAttribute("states", State.values());
        return "form_provider";
    }

    @PostMapping("/provider/update")
    public String updateProvider(@Valid @ModelAttribute("provider") ServiceProvider provider) {
        if (provider.getUserId() != null && provider.getUserId().getId() != null) {
            User user = userService.getUserById(provider.getUserId().getId());
            provider.setUserId(user);
        }

        ServiceProvider old = serviceProviderService.getProviderById(provider.getId());
        provider.setCreatedAt(old.getCreatedAt());

        provider.setUpdatedAt(new java.util.Date());
        serviceProviderService.updateProvider(provider);
        return "redirect:/providers";
    }
}
