/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.repositories;

import java.util.List;

import com.tba.pojo.ServiceProvider;

/**
 *
 * @author HP Zbook 15
 */
public interface ServiceProviderRepository {

    List<ServiceProvider> getAllServiceProviders();

    void deleteProvider(Integer id);
    
    ServiceProvider getServiceProviderByUserId(int userId);

    void addProvider(ServiceProvider u);

    void updateProvider(ServiceProvider provider);

    ServiceProvider getProviderById(Integer id);
}
