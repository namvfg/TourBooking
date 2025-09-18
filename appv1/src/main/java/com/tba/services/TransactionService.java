/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.ServicePost;
import com.tba.pojo.Transaction;
import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    void deleteTransaction(Integer id);

    void add(Transaction transaction);

    void update(ServicePost servicePost, Transaction transaction);

    List<Transaction> getTransactionsByPostId(int postId);

    List<Transaction> getTransactionsByUserId(int userId);

    Transaction getTransactionByTransactionCode(String transactionCode);
}
