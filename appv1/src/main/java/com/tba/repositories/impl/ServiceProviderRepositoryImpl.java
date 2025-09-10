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

    @Override
    public ServiceProvider getServiceProviderByUserId(int userId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<ServiceProvider> q = b.createQuery(ServiceProvider.class);
        Root root = q.from(ServiceProvider.class);
        q.select(root);
        q.where(b.equal(root.get("userId").get("id"), userId));
        return session.createQuery(q).uniqueResult();
    }

    @Override
    public void addProvider(ServiceProvider p) {
        Session s = this.factory.getObject().getCurrentSession();
        if (p.getId() != null) {
            throw new IllegalArgumentException("ID must not be set manually when using GenerationType.IDENTITY");
        }
        s.persist(p);
    }

    @Override
    public void updateProvider(ServiceProvider provider) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(provider);
    }

    @Override
    public ServiceProvider getProviderById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(ServiceProvider.class, id);
    }
}
