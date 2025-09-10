/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Admin
 */
public class ProviderRegisterRequestDTO extends UserRegisterRequestDTO{

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(min = 2, max = 20, message = "Tên công ty phải từ 2 đến 20 ký tự")
    private String companyName;

    public ProviderRegisterRequestDTO() {
    }

    public ProviderRegisterRequestDTO(String companyName, String firstName, String lastName, String email, String username, String password, MultipartFile avatar, String address, String phoneNumber) {
        super(firstName, lastName, email, username, password, avatar, address, phoneNumber);
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
