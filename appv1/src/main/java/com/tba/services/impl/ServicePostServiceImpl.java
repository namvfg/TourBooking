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
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicePostServiceImpl implements ServicePostService {

    @Autowired
    private ServicePostRepository servicePostRepository;

    @Override
    public List<ServicePost> getServicePosts(Map<String, String> params) {
        return servicePostRepository.getServicePosts(params);
    }

    @Override
    public void softDeleteServicePost(Integer id) {
        servicePostRepository.softDeleteServicePost(id);
    }

    @Override
    public ServicePost getServicePostById(Integer id) {
        return servicePostRepository.getServicePostById(id);
    }

    @Override
    public void addServicePost(ServicePost post) {
        servicePostRepository.addServicePost(post);
    }

    @Override
    public void updateServicePost(ServicePost post) {
        servicePostRepository.updateServicePost(post);
    }

    @Override
    public List<ServicePost> getServicePostsPaged(int page, int size) {
        return servicePostRepository.getServicePostsPaged(page, size);
    }

    @Override
    public long countServicePosts() {
        return servicePostRepository.countServicePosts();
    }
    
    @Override
    public long countServicePostsWithFilters(Map<String, String> params) {
        return servicePostRepository.countServicePostsWithFilters(params);
    }
}
