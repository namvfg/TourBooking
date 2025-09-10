/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tba.enums.ServiceType;
import com.tba.enums.State;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class ServicePermissionResponseDTO {
    private Integer id;
    private ServiceType serviceType;
    private State state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date updatedDate;
    private Integer serviceProviderId;
    private String serviceProviderCompanyName;

    public ServicePermissionResponseDTO() {
    }

    public ServicePermissionResponseDTO(Integer id, ServiceType serviceType, State state, Date createdDate, Date updatedDate, Integer serviceProviderId, String serviceProviderCompanyName) {
        this.id = id;
        this.serviceType = serviceType;
        this.state = state;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.serviceProviderId = serviceProviderId;
        this.serviceProviderCompanyName = serviceProviderCompanyName;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
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
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the serviceProviderId
     */
    public Integer getServiceProviderId() {
        return serviceProviderId;
    }

    /**
     * @param serviceProviderId the serviceProviderId to set
     */
    public void setServiceProviderId(Integer serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    /**
     * @return the serviceProviderCompanyName
     */
    public String getServiceProviderCompanyName() {
        return serviceProviderCompanyName;
    }

    /**
     * @param serviceProviderCompanyName the serviceProviderCompanyName to set
     */
    public void setServiceProviderCompanyName(String serviceProviderCompanyName) {
        this.serviceProviderCompanyName = serviceProviderCompanyName;
    }
    
    
    
}
