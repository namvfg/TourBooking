package com.tba.repositories.impl;

import com.tba.pojo.Transportation;
import com.tba.repositories.TransportationRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TransportationRepositoryImpl implements TransportationRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    @Transactional
    public void addTransportation(Transportation transportation) {
        Session session = factory.getObject().getCurrentSession();
        session.persist(transportation);
    }
}