/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServicePost;
import com.tba.repositories.ServicePostRepository;
import com.tba.services.ServicePostService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicePostServiceImpl implements ServicePostService {
    @Autowired
    private ServicePostRepository servicePostRepository;

    @Override
    public List<ServicePost> getAllServicePosts() {
        return servicePostRepository.getAllServicePosts();
    }
}
