/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories.impl;

import com.tba.enums.ServiceType;
import com.tba.pojo.ServicePermission;
import com.tba.repositories.ServicePermissionRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public class ServicePermissionRepositoryImpl implements ServicePermissionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public void addServicePermission(ServicePermission servicePermission) {
        Session s = this.factory.getObject().getCurrentSession();
        if (servicePermission.getId() != null) {
            throw new IllegalArgumentException("ID must not be set manually when using GenerationType.IDENTITY");
        }
        s.persist(servicePermission);
    }

    @Override
    public List<ServicePermission> getPermissionsByProviderId(Integer providerId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<ServicePermission> cq = cb.createQuery(ServicePermission.class);
        Root<ServicePermission> root = cq.from(ServicePermission.class);
        cq.select(root).where(cb.equal(root.get("serviceProviderId").get("id"), providerId));

        return s.createQuery(cq).getResultList();
    }

    @Override
    public void updateServicePermission(ServicePermission servicePermission) {
        Session s = this.factory.getObject().getCurrentSession();
        s.merge(servicePermission);
    }

    @Override
    public void addAllServicePermissions(List<ServicePermission> servicePermissions) {
        Session session = this.factory.getObject().getCurrentSession();
        for (int i = 0; i < servicePermissions.size(); i++) {
            session.persist(servicePermissions.get(i));
        }
    }

    @Override
    public boolean existsByProviderAndServiceType(int providerId, ServiceType type) {
        Session session = this.factory.getObject().getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ServicePermission> root = cq.from(ServicePermission.class);
        Predicate byProvider = cb.equal(root.get("serviceProviderId").get("id"), providerId);
        Predicate byType = cb.equal(root.get("serviceType"), type);
        cq.select(cb.count(root))
                .where(cb.and(byProvider, byType));

        Long count = session.createQuery(cq).getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public ServicePermission getPermissionById(Integer id) {
        Session s = this.factory.getObject().getCurrentSession();
        return s.get(ServicePermission.class, id);
    }

    @Override
    public List<ServicePermission> getAllPermissions() {
        Session s = this.factory.getObject().getCurrentSession();
        return s.createQuery("FROM ServicePermission", ServicePermission.class).getResultList();
    }

}
