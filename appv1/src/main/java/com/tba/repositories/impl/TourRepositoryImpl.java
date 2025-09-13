package com.tba.repositories.impl;

import com.tba.pojo.Tour;
import com.tba.repositories.TourRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TourRepositoryImpl implements TourRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    @Transactional
    public void addTour(Tour tour) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(tour);
    }
}