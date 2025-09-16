/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.request;

import com.tba.enums.TransactionType;

/**
 *
 * @author Admin
 */
public class TransactionRequestDTO {

    private int slotQuantity;
    private TransactionType transactionType;

    public TransactionRequestDTO() {
    }

    public TransactionRequestDTO(int slotQuantity, TransactionType transactionType) {
        this.slotQuantity = slotQuantity;
        this.transactionType = transactionType;
    }

    /**
     * @return the slotQuantity
     */
    public int getSlotQuantity() {
        return slotQuantity;
    }

    /**
     * @param slotQuantity the slotQuantity to set
     */
    public void setSlotQuantity(int slotQuantity) {
        this.slotQuantity = slotQuantity;
    }

    /**
     * @return the transactionType
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @param transactionType the transactionType to set
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
