/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.repositories.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.Transaction;
import com.tba.repositories.TransactionRepository;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.tba.enums.PaymentStatus;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Transaction> getAllTransactions() {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);
        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    @Override
    public void deleteTransaction(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        Transaction transaction = session.get(Transaction.class, id);
        if (transaction != null) {
            session.delete(transaction);
        }
    }

    @Override
    public void add(Transaction transaction) {
        Session s = this.factory.getObject().getCurrentSession();
        if (transaction.getId() != null) {
            throw new IllegalArgumentException("ID must not be set manually when using GenerationType.IDENTITY");
        }
        s.persist(transaction);
    }

    @Override
    public void update(Transaction transaction) {
        Session s = this.factory.getObject().getCurrentSession();
        s.merge(transaction);
    }

    @Override
    public Transaction getTransactionByTransactionCode(String transactionCode) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        Predicate codeMatch = cb.equal(root.get("transactionCode"), transactionCode);
        cq.select(root).where(codeMatch);

        List<Transaction> result = session.createQuery(cq).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Object[]> getTopProvidersRevenue(Integer month, Integer year, int limit) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT s.serviceProviderId.id, s.serviceProviderId.companyName, SUM(t.totalAmount) "
                + "FROM Transaction t JOIN t.servicePostId s "
                + "WHERE t.paymentStatus = :status";
        if (month != null && year != null) {
            hql += " AND MONTH(t.createdDate) = :month AND YEAR(t.createdDate) = :year";
        }
        hql += " GROUP BY s.serviceProviderId.id, s.serviceProviderId.companyName ORDER BY SUM(t.totalAmount) DESC";
        org.hibernate.query.Query q = session.createQuery(hql);
        q.setParameter("status", com.tba.enums.PaymentStatus.PAID);

        if (month != null && year != null) {
            q.setParameter("month", month);
            q.setParameter("year", year);
        }
        q.setMaxResults(limit);
        return q.getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByPostId(int postId) {
        Session session = factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = builder.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        query.select(root);
        query.where(
                builder.and(
                        builder.equal(root.get("servicePostId").get("id"), postId),
                        builder.equal(root.get("paymentStatus"), PaymentStatus.PAID)
                )
        );
        query.orderBy(builder.desc(root.get("createdDate")));

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        Session session = factory.getObject().getCurrentSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = builder.createQuery(Transaction.class);
        Root<Transaction> root = query.from(Transaction.class);

        query.select(root)
                .where(builder.equal(root.get("userId").get("id"), userId));

        return session.createQuery(query).getResultList();
    }
}
