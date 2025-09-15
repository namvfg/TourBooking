/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ProviderRating;
import com.tba.repositories.ProviderRatingRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProviderRatingRepositoryImpl implements ProviderRatingRepository {
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<ProviderRating> getRatingsByProviderId(int providerId) {
        Session session = factory.getObject().getCurrentSession();
        String hql = "FROM ProviderRating pr WHERE pr.serviceProviderId.id = :providerId AND pr.isDeleted = false ORDER BY pr.createdDate DESC";
        return session.createQuery(hql, ProviderRating.class)
                .setParameter("providerId", providerId).getResultList();
    }

    @Override
    public ProviderRating findByUserAndProvider(int userId, int providerId) {
        Session session = factory.getObject().getCurrentSession();
        String hql = "FROM ProviderRating pr WHERE pr.userId.id = :userId AND pr.serviceProviderId.id = :providerId AND pr.isDeleted = false";
        return session.createQuery(hql, ProviderRating.class)
                .setParameter("userId", userId)
                .setParameter("providerId", providerId)
                .uniqueResult();
    }

    @Override
    public void addRating(ProviderRating rating) {
        factory.getObject().getCurrentSession().persist(rating);
    }

    @Override
    public void updateRating(ProviderRating rating) {
        factory.getObject().getCurrentSession().merge(rating);
    }
}