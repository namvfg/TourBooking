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
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void deleteTransaction(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        Transaction transaction = session.get(Transaction.class, id);
        if (transaction != null) {
            session.delete(transaction);
        }
    }
}
