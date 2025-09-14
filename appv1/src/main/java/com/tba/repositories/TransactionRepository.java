/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.repositories;

/**
 *
 * @author HP Zbook 15
 */
import com.tba.pojo.Transaction;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> getAllTransactions();
    
    void deleteTransaction(Integer id);
    
    void add(Transaction transaction);
    
    void update(Transaction transaction);
    
    Transaction getTransactionByTransactionCode(String transactionCode);
}
