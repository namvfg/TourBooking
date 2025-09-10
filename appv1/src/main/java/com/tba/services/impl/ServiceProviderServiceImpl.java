/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

/**
 *
 * @author HP Zbook 15
 */
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.repositories.ServiceProviderRepository;
import com.tba.services.ServiceProviderService;
import com.tba.services.UserService;

@Service
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.getAllServiceProviders();
    }

    @Override
    public void deleteProvider(Integer id) {
        serviceProviderRepository.deleteProvider(id);
    }

    @Override
    public ServiceProvider getServiceProviderByUserId(int userId) {
        return this.serviceProviderRepository.getServiceProviderByUserId(userId);
    }

    @Override
    public void addProvider(User u, ServiceProvider p) {
        this.userService.addUser(u);
        this.serviceProviderRepository.addProvider(p);
    }

    @Override
    public void updateProvider(ServiceProvider provider) {
        serviceProviderRepository.updateProvider(provider);
    }

    @Override
    public ServiceProvider getProviderById(Integer id) {
        return serviceProviderRepository.getProviderById(id);
    }
}
