package com.tba.services.impl;

import com.tba.pojo.ServicePost;
import com.tba.pojo.Tour;
import com.tba.repositories.TourRepository;
import com.tba.services.ServicePostService;
import com.tba.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;
    
    @Autowired
    private ServicePostService servicePostService;

    @Override
    public void addTour(ServicePost post, Tour tour) {
        servicePostService.addServicePost(post);
        tour.setServicePostId(post.getId());
        tourRepository.addTour(tour);
    }


}