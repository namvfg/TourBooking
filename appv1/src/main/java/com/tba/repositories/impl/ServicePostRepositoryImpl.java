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
@Transactional
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
}
