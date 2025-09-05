/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServiceProvider;
import com.tba.repositories.ServiceProviderRepository;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceProviderRepositoryImpl implements ServiceProviderRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ServiceProvider> getAllServiceProviders() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ServiceProvider> cq = cb.createQuery(ServiceProvider.class);
        Root<ServiceProvider> root = cq.from(ServiceProvider.class);
        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    @Override
    @Transactional
    public void deleteProvider(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        ServiceProvider provider = session.get(ServiceProvider.class, id);
        if (provider != null) {
            session.delete(provider);
        }
    }
}
