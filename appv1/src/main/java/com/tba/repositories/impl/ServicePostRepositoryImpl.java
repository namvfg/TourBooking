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
import com.tba.pojo.Room;
import com.tba.pojo.Tour;
import com.tba.pojo.Transportation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.query.Query;

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
    public List<ServicePost> getServicePosts(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ServicePost> cq = cb.createQuery(ServicePost.class);
        Root<ServicePost> root = cq.from(ServicePost.class);
        cq.select(root);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isDeleted"), false));

        String keyword = params.get("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        String serviceType = params.get("serviceType");
        if (serviceType != null && !serviceType.isEmpty()) {
            predicates.add(cb.equal(root.get("serviceType"), serviceType));
        }

        String minPrice = params.get("minPrice");
        if (minPrice != null && !minPrice.isEmpty()) {
            try {
                predicates.add(cb.ge(root.get("price"), Double.parseDouble(minPrice)));
            } catch (NumberFormatException ignored) {
            }
        }

        String maxPrice = params.get("maxPrice");
        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                predicates.add(cb.le(root.get("price"), Double.parseDouble(maxPrice)));
            } catch (NumberFormatException ignored) {
            }
        }

        String startDate = params.get("startDate");
        if (startDate != null && !startDate.isEmpty() && serviceType != null && !serviceType.isEmpty()) {

            try {
                java.sql.Date date = java.sql.Date.valueOf(startDate.substring(0, 10));
                if ("ROOM".equals(serviceType)) {
                    Join<Object, Object> roomJoin = root.join("room", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, roomJoin.get("startDate")), date));
                } else if ("TOUR".equals(serviceType)) {
                    Join<Object, Object> tourJoin = root.join("tour", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, tourJoin.get("startDate")), date));
                } else if ("TRANSPORTATION".equals(serviceType)) {
                    Join<Object, Object> tranJoin = root.join("transportation", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, tranJoin.get("startDate")), date));
                }
            } catch (Exception ignored) {
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        cq.orderBy(cb.asc(root.get("id")));
        Query query = session.createQuery(cq);
        if (params != null && params.containsKey("page") && params.containsKey("size")) {
            try {
                int page = Integer.parseInt(params.get("page"));
                int size = Integer.parseInt(params.get("size"));
                query.setFirstResult(page * size);
                query.setMaxResults(size);
            } catch (NumberFormatException ignored) {
            }
        }

        return query.getResultList();
    }

    public long countServicePostsWithFilters(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ServicePost> root = cq.from(ServicePost.class);
        cq.select(cb.count(root));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isDeleted"), false));

        // FILTER giống hệt như getServicePosts
        String keyword = params.get("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }
        String serviceType = params.get("serviceType");
        if (serviceType != null && !serviceType.isEmpty()) {
            predicates.add(cb.equal(root.get("serviceType"), serviceType));
        }
        String minPrice = params.get("minPrice");
        if (minPrice != null && !minPrice.isEmpty()) {
            try {
                predicates.add(cb.ge(root.get("price"), Double.parseDouble(minPrice)));
            } catch (NumberFormatException ignored) {
            }
        }
        String maxPrice = params.get("maxPrice");
        if (maxPrice != null && !maxPrice.isEmpty()) {
            try {
                predicates.add(cb.le(root.get("price"), Double.parseDouble(maxPrice)));
            } catch (NumberFormatException ignored) {
            }
        }
        String startDate = params.get("startDate");
        if (startDate != null && !startDate.isEmpty() && serviceType != null && !serviceType.isEmpty()) {
            try {
                java.sql.Date date = java.sql.Date.valueOf(startDate.substring(0, 10));
                if ("ROOM".equals(serviceType)) {
                    Join<Object, Object> roomJoin = root.join("room", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, roomJoin.get("startDate")), date));
                } else if ("TOUR".equals(serviceType)) {
                    Join<Object, Object> tourJoin = root.join("tour", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, tourJoin.get("startDate")), date));
                } else if ("TRANSPORTATION".equals(serviceType)) {
                    Join<Object, Object> tranJoin = root.join("transportation", JoinType.LEFT);
                    predicates.add(cb.equal(cb.function("date", java.sql.Date.class, tranJoin.get("startDate")), date));
                }
            } catch (Exception ignored) {
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        Query<Long> query = session.createQuery(cq);
        return query.getSingleResult();
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

    @Override
    public List<ServicePost> getServicePostsByProviderIdPaged(int providerId, int page, int size) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ServicePost> cq = cb.createQuery(ServicePost.class);
        Root<ServicePost> root = cq.from(ServicePost.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isDeleted"), false));
        predicates.add(cb.equal(root.get("serviceProviderId").get("id"), providerId));

        cq.select(root).where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("id")));

        Query<ServicePost> query = session.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public long countServicePostsByProviderId(int providerId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ServicePost> root = cq.from(ServicePost.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isDeleted"), false));
        predicates.add(cb.equal(root.get("serviceProviderId").get("id"), providerId));

        cq.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(cq).getSingleResult();
    }

    @Override
    public long countServicePostsByMonthYear(Integer month, Integer year) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(s) FROM ServicePost s WHERE s.isDeleted = false";
        if (month != null && year != null) {
            hql += " AND MONTH(s.createdDate) = :month AND YEAR(s.createdDate) = :year";
        }
        org.hibernate.query.Query q = session.createQuery(hql);
        if (month != null && year != null) {
            q.setParameter("month", month);
            q.setParameter("year", year);
        }
        return (Long) q.getSingleResult();
    }

    @Override
    public Map<String, Long> countServiceByType(Integer month, Integer year) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT s.serviceType, COUNT(s) FROM ServicePost s WHERE s.isDeleted = false";
        if (month != null && year != null) {
            hql += " AND MONTH(s.createdDate) = :month AND YEAR(s.createdDate) = :year";
        }
        hql += " GROUP BY s.serviceType";
        Query q = session.createQuery(hql);
        if (month != null && year != null) {
            q.setParameter("month", month);
            q.setParameter("year", year);
        }
        List<Object[]> results = q.getResultList();
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : results) {
            map.put(row[0].toString(), (Long) row[1]);
        }
        return map;
    }

    @Override
    public Map<String, Long> revenueByServiceType(Integer month, Integer year) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT s.serviceType, SUM(t.totalAmount) "
                + "FROM Transaction t JOIN t.servicePostId s "
                + "WHERE t.paymentStatus = :status";
        if (month != null && year != null) {
            hql += " AND MONTH(t.createdDate) = :month AND YEAR(t.createdDate) = :year";
        }
        hql += " GROUP BY s.serviceType";
        Query q = session.createQuery(hql);
        q.setParameter("status", com.tba.enums.PaymentStatus.PAID);
        if (month != null && year != null) {
            q.setParameter("month", month);
            q.setParameter("year", year);
        }
        List<Object[]> results = q.getResultList();
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : results) {
            map.put(row[0].toString(), row[1] == null ? 0L : ((Number) row[1]).longValue());
        }
        return map;
    }

}
