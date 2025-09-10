/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tba.enums.State;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class ServiceProviderResponseDTO {

    private Integer providerId;
    private String companyName;
    private State state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date providerCreatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date providerUpdatedAt;

    public ServiceProviderResponseDTO() {
    }

    public ServiceProviderResponseDTO(Integer id, String companyName, State state, Date createdAt, Date updatedAt) {
        this.providerId = id;
        this.companyName = companyName;
        this.state = state;
        this.providerCreatedAt = createdAt;
        this.providerUpdatedAt = updatedAt;
    }

    /**
     * @return the providerId
     */
    public Integer getProviderId() {
        return providerId;
    }

    /**
     * @param providerId the providerId to set
     */
    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @return the providerCreatedAt
     */
    public Date getProviderCreatedAt() {
        return providerCreatedAt;
    }

    /**
     * @param providerCreatedAt the providerCreatedAt to set
     */
    public void setProviderCreatedAt(Date providerCreatedAt) {
        this.providerCreatedAt = providerCreatedAt;
    }

    /**
     * @return the providerUpdatedAt
     */
    public Date getProviderUpdatedAt() {
        return providerUpdatedAt;
    }

    /**
     * @param providerUpdatedAt the providerUpdatedAt to set
     */
    public void setProviderUpdatedAt(Date providerUpdatedAt) {
        this.providerUpdatedAt = providerUpdatedAt;
    }
}
