/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServiceProvider;
import java.util.List;
public interface ServiceProviderService {
    List<ServiceProvider> getAllServiceProviders();
    
    void deleteProvider(Integer id);
    
    ServiceProvider getServiceProviderByUserId(int userId);
}
