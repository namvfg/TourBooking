package com.tba.services.impl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author HP Zbook 15
 */
import com.tba.repositories.UserRepository;
import com.tba.repositories.ServicePostRepository;
import com.tba.repositories.TransactionRepository;
import com.tba.services.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ServicePostRepository servicePostRepo;
    @Autowired
    private TransactionRepository transactionRepo;

    @Override
    public Map<String, Object> getStatistics(Integer month, Integer year) {
        long userCount = userRepo.countByRoleAndMonthYear("USER", month, year);
        long providerCount = userRepo.countByRoleAndMonthYear("PROVIDER", month, year);
        long serviceCount = servicePostRepo.countServicePostsByMonthYear(month, year);

        Map<String, Long> serviceTypeCount = servicePostRepo.countServiceByType(month, year);
        Map<String, Long> revenueByType = servicePostRepo.revenueByServiceType(month, year);

        Map<String, Object> res = new HashMap<>();
        res.put("userCount", userCount);
        res.put("providerCount", providerCount);
        res.put("serviceCount", serviceCount);
        res.put("serviceTypeCount", serviceTypeCount);
        res.put("revenueByType", revenueByType);

        return res;
    }

    @Override
    public Map<String, Long> revenueByServiceType(Integer month, Integer year) {
        return servicePostRepo.revenueByServiceType(month, year);
    }
}
