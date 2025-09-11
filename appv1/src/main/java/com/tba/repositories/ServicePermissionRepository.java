/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories;

import com.tba.enums.ServiceType;
import com.tba.pojo.ServicePermission;
import java.util.List;

/**
 *
 * @author Admin
 */
public interface ServicePermissionRepository {

    void addServicePermission(ServicePermission servicePermission);

    void addAllServicePermissions(List<ServicePermission> servicePermissions);

    List<ServicePermission> getPermissionsByProviderId(Integer providerId);

    void updateServicePermission(ServicePermission servicePermission);

    boolean existsByProviderAndServiceType(int providerId, ServiceType type);
    
    ServicePermission getPermissionById(Integer id);
    
    List<ServicePermission> getAllPermissions();
}
