/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.services.AdminStatisticsService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
public class IndexController {

    @Autowired
    private AdminStatisticsService statisticsService;

    @GetMapping("/")
    public String index(
            @RequestParam(value = "monthYear", required = false) String monthYear,
            Model model) {

        Integer month = null, year = null;
        if (monthYear != null && !monthYear.isEmpty()) {
            String[] arr = monthYear.split("-");
            if (arr.length == 2) {
                try {
                    year = Integer.parseInt(arr[0]);
                    month = Integer.parseInt(arr[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Không parse được tháng/năm: " + monthYear);
                }
            }
        }
        System.out.println("monthYear=" + monthYear + ", month=" + month + ", year=" + year);

        Map<String, Object> stats = statisticsService.getStatistics(month, year);
        model.addAllAttributes(stats);


        model.addAttribute("serviceTypeCountJson", stats.get("serviceTypeCount"));
        model.addAttribute("revenueByTypeJson", stats.get("revenueByType"));

        model.addAttribute("month", month);
        model.addAttribute("year", year);
        return "index";
    }
}