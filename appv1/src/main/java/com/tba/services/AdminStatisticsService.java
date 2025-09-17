/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tba.services;

/**
 *
 * @author HP Zbook 15
 */
import java.util.Map;

public interface AdminStatisticsService {
    Map<String, Object> getStatistics(Integer month, Integer year);
    
    Map<String, Long> revenueByServiceType(Integer month, Integer year);
}
