/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories.impl;

import com.tba.pojo.ServiceProvider;
import com.tba.repositories.ProviderRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public class ProviderRepositoryImpl implements ProviderRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void addProvider(ServiceProvider p) {
        Session s = this.factory.getObject().getCurrentSession();
        if (p.getId() != null) {
            throw new IllegalArgumentException("ID must not be set manually when using GenerationType.IDENTITY");
        }
        s.persist(p);
        s.flush();
    }
}
