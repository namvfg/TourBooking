/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.Transaction;
import com.tba.repositories.TransactionRepository;
import com.tba.services.TransactionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    @Override
    public void deleteTransaction(Integer id) {
        transactionRepository.deleteTransaction(id);
    }

    @Override
    public void add(Transaction transaction) {
        this.transactionRepository.add(transaction);
    }

    @Override
    public void update(Transaction transaction) {
        this.transactionRepository.update(transaction);
    }
}
