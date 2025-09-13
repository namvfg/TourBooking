package com.tba.services.impl;

import com.tba.pojo.Tour;
import com.tba.repositories.TourRepository;
import com.tba.services.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;

    @Override
    public void addTour(Tour tour) {
        tourRepository.addTour(tour);
    }
}