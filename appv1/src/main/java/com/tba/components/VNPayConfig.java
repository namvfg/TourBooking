/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.components;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class VNPayConfig {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.vnpUrl}")
    private String vnpUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    public String createVNPayUrl(BigDecimal amount, String orderInfo, String ipAddress, String transactionCode)
            throws UnsupportedEncodingException {

        String vnp_TxnRef = transactionCode != null ? transactionCode : String.valueOf(System.currentTimeMillis());
        String vnp_Amount = String.valueOf(amount.multiply(BigDecimal.valueOf(100)).intValue());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        System.out.println("--- VNPAY DEBUG INFO ---");
        System.out.println("vnp_Amount: " + vnp_Params.get("vnp_Amount"));
        System.out.println("vnp_OrderInfo: " + orderInfo);
        System.out.println("vnp_TxnRef: " + transactionCode);
        System.out.println("vnp_ReturnUrl: " + returnUrl);
        System.out.println("--- END DEBUG INFO ---");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString());
                hashData.append(fieldName).append("=").append(encodedValue);
                query.append(fieldName).append("=").append(encodedValue);
                if (itr.hasNext()) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        System.out.println("--- VNPAY ENCODED QUERY ---");
        System.out.println("HashData: " + hashData.toString());
        System.out.println("Query   : " + query.toString());
        System.out.println("--- END ENCODED QUERY ---");

        return vnpUrl + "?" + query.toString();
    }

    public boolean verifySignature(Map<String, String> vnpParams, String receivedHash) {
        Map<String, String> filteredParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (!entry.getKey().equals("vnp_SecureHash") && !entry.getKey().equals("vnp_SecureHashType")) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }

        List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = filteredParams.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }

        try {
            String calculatedHash = hmacSHA512(hashSecret, hashData.toString());
            return calculatedHash.equalsIgnoreCase(receivedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getVnpUrl() {
        return vnpUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

}
