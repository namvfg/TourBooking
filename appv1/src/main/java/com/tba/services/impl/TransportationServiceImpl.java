
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
import com.tba.pojo.Transportation;
import com.tba.repositories.TransportationRepository;
import com.tba.services.ServicePostService;
import com.tba.services.TransportationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransportationServiceImpl implements TransportationService {

    @Autowired
    private TransportationRepository transportationRepository;
    
    @Autowired
    private ServicePostService servicePostService;

    @Override
    public void addTransportation(ServicePost post, Transportation transportation) {
        servicePostService.addServicePost(post);
        transportation.setServicePostId(post.getId());
        transportationRepository.addTransportation(transportation);
    }


}