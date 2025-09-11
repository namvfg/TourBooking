package com.tba.repositories.impl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServicePost;
import com.tba.repositories.ServicePostRepository;
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
public class ServicePostRepositoryImpl implements ServicePostRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    @Transactional
    public void softDeleteServicePost(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        ServicePost servicePost = session.get(ServicePost.class, id);
        if (servicePost != null) {
            servicePost.setIsDeleted(true);
            session.update(servicePost);
        }
    }

    @Override
    public List<ServicePost> getAllServicePosts() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ServicePost> cq = cb.createQuery(ServicePost.class);
        Root<ServicePost> root = cq.from(ServicePost.class);
        cq.select(root).where(cb.equal(root.get("isDeleted"), false));
        return session.createQuery(cq).getResultList();
    }

    @Override
    public ServicePost getServicePostById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(ServicePost.class, id);
    }

    @Override
    @Transactional
    public void addServicePost(ServicePost post) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(post);
    }

    @Override
    @Transactional
    public void updateServicePost(ServicePost post) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(post);
    }

    @Override
    public List<ServicePost> getServicePostsPaged(int page, int size) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ServicePost> cq = cb.createQuery(ServicePost.class);
        Root<ServicePost> root = cq.from(ServicePost.class);
        cq.select(root).where(cb.equal(root.get("isDeleted"), false));
        return session.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countServicePosts() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ServicePost> root = cq.from(ServicePost.class);
        cq.select(cb.count(root)).where(cb.equal(root.get("isDeleted"), false));
        return session.createQuery(cq).getSingleResult();
    }
}
