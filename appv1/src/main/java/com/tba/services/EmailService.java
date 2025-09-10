/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author Admin
 */
public interface EmailService {

    // Provider
    void sendProviderPending(String to, String companyName);

    void sendProviderApproved(String to, String companyName);

    void sendProviderRejected(String to, String companyName);

    void sendProviderDisabled(String to, String companyName);

    // Permission
    void sendPermissionPending(String to, String companyName, String serviceType);

    void sendPermissionApproved(String to, String companyName, String serviceType);

    void sendPermissionRejected(String to, String companyName, String serviceType);

    void sendPermissionDisabled(String to, String companyName, String serviceType);
}
