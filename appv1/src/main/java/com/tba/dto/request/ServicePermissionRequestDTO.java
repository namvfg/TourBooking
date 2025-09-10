/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.request;

import com.tba.enums.ServiceType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 *
 * @author Admin
 */
public class ServicePermissionRequestDTO {

    @NotEmpty(message = "Danh sách serviceType không được để trống")
    private List<ServiceType> serviceTypes;

    public ServicePermissionRequestDTO() {
    }

    public ServicePermissionRequestDTO(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    /**
     * @return the serviceTypes
     */
    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    /**
     * @param serviceTypes the serviceTypes to set
     */
    public void setServiceTypes(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }
}
