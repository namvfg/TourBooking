/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public class ProviderUpdateRequestDTO extends UserUpdateRequestDTO {

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 50, message = "Tên công ty phải từ 2 đến 20 ký tự")
    private String companyName;

    public ProviderUpdateRequestDTO() {
    }

    public ProviderUpdateRequestDTO(String companyName, String firstName, String lastName, String email, String address, String phoneNumber, MultipartFile avatar) {
        super(firstName, lastName, email, address, phoneNumber, avatar);
        this.companyName = companyName;
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
}
