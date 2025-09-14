/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author HP Zbook 15
 */
import java.util.List;

import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;

public interface ServiceProviderService {

    List<ServiceProvider> getAllServiceProviders();

    void deleteProvider(Integer id);

    ServiceProvider getServiceProviderByUserId(int userId);

    void addProvider(User u, ServiceProvider p);

    void updateProvider(ServiceProvider provider);

    ServiceProvider getProviderById(Integer id);
    
    boolean existsByCompanyName(String companyName);
}
