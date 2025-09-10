/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

import com.tba.enums.ServiceType;
import com.tba.pojo.ServicePermission;
import com.tba.repositories.ServicePermissionRepository;
import com.tba.services.ServicePermissionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class ServicePermissionServiceImpl implements ServicePermissionService {

    @Autowired
    private ServicePermissionRepository servicePermissionRepository;

    @Override
    public void addServicePermission(ServicePermission servicePermission) {
        this.servicePermissionRepository.addServicePermission(servicePermission);
    }

    @Override
    public List<ServicePermission> getPermissionsByProviderId(Integer providerId) {
        return this.servicePermissionRepository.getPermissionsByProviderId(providerId);
    }

    @Override
    public void updateServicePermission(ServicePermission servicePermission) {
        this.servicePermissionRepository.updateServicePermission(servicePermission);
    }

    @Override
    public void addAllServicePermissions(List<ServicePermission> servicePermissions) {
        this.servicePermissionRepository.addAllServicePermissions(servicePermissions);
    }

    @Override
    public boolean existsByProviderAndServiceType(int providerId, ServiceType type) {
        return this.servicePermissionRepository.existsByProviderAndServiceType(providerId, type);
    }

}
