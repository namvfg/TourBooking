/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.response;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class TransactionResponseDTO {

    private int id;
    private int slotQuantity;
    private BigDecimal totalAmount;
    private Date createdDate;
    private String username;
    private String email;
    private String fullName;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(int id, int slotQuantity, BigDecimal totalAmount, Date createdDate, String username, String email, String fullName) {
        this.id = id;
        this.slotQuantity = slotQuantity;
        this.totalAmount = totalAmount;
        this.createdDate = createdDate;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
