/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

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

@Service
public class ServiceProviderServiceImpl implements ServiceProviderService {
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Override
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.getAllServiceProviders();
    }
}
