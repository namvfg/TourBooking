/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.enums.ServiceType;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiEnumsController {

    @GetMapping("/enums/service-types")
    public ResponseEntity<List<String>> getServiceTypes() {
        List<String> types = Arrays.stream(ServiceType.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(types);
    }
}
