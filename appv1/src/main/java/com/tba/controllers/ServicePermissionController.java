package com.tba.controllers;

import com.tba.enums.State;
import com.tba.pojo.ServicePermission;
import com.tba.pojo.User;
import com.tba.services.ServicePermissionService;
import com.tba.services.EmailService;
import com.tba.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author Admin
 */

@Controller
public class ServicePermissionController {

    @Autowired
    private ServicePermissionService permissionService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;


    @GetMapping("/admin/permissions")
    public String listPermissions(Model model) {
        List<ServicePermission> permissions = permissionService.getAllPermissions();
        model.addAttribute("permissions", permissions);
        model.addAttribute("states", State.values());
        model.addAttribute("active", "permissions");
        return "table_permission";
    }


    @GetMapping("/admin/permission/update/{id}")
    public String showUpdatePermission(@PathVariable("id") Integer id, Model model) {
        ServicePermission permission = permissionService.getPermissionById(id);
        model.addAttribute("permission", permission);
        model.addAttribute("states", State.values());
        model.addAttribute("active", "permissions");
        return "form_permission";
    }


    @PostMapping("/admin/permission/update")
    public String updatePermission(@ModelAttribute("permission") ServicePermission permission) {
        ServicePermission old = permissionService.getPermissionById(permission.getId());
        permission.setServiceProviderId(old.getServiceProviderId());
        permission.setServiceType(old.getServiceType());
        permission.setCreatedDate(old.getCreatedDate());
        permission.setUpdatedDate(new java.util.Date());
        permissionService.updateServicePermission(permission);

        User u = userService.getUserByProviderId(permission.getServiceProviderId().getId());
        

        if (permission.getState()==State.ACTIVE) {
            String to = u.getEmail();
            String companyName = permission.getServiceProviderId().getCompanyName();
            String serviceType = old.getServiceType().name(); 
            emailService.sendPermissionApproved(to, companyName, serviceType);
        }

        return "redirect:/admin/permissions";
    }
}