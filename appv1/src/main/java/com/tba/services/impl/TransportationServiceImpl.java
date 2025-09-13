package com.tba.services.impl;

import com.tba.pojo.Transportation;
import com.tba.repositories.TransportationRepository;
import com.tba.services.TransportationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransportationServiceImpl implements TransportationService {

    @Autowired
    private TransportationRepository transportationRepository;

    @Override
    public void addTransportation(Transportation transportation) {
        transportationRepository.addTransportation(transportation);
    }
}