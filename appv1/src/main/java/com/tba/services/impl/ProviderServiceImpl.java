/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

import com.tba.pojo.ServiceProvider;
import com.tba.pojo.User;
import com.tba.repositories.ProviderRepository;
import com.tba.services.ProviderService;
import com.tba.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private UserService userService;
    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public void addProvider(User u, ServiceProvider p) {
        this.userService.addUser(u);
        this.providerRepository.addProvider(p);
    }
}
