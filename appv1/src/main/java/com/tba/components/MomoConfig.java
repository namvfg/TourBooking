/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.components;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */
@Component
public class MomoConfig {

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirectUrl}")
    private String redirectUrl;

    @Value("${momo.ipnUrl}")
    private String ipnUrl;

    public String createMoMoPaymentUrl(long amount, String orderId, String orderInfo) throws Exception {
        String requestId = UUID.randomUUID().toString();

        JSONObject json = new JSONObject();
        json.put("partnerCode", partnerCode);
        json.put("accessKey", accessKey);
        json.put("requestId", requestId);
        json.put("amount", String.valueOf(amount));
        json.put("orderId", orderId);
        json.put("orderInfo", orderInfo);
        json.put("redirectUrl", redirectUrl);
        json.put("ipnUrl", ipnUrl);
        json.put("extraData", "");
        json.put("requestType", "captureWallet");
        json.put("lang", "vi");

        String rawHash = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData="
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=captureWallet";

        String signature = hmacSHA256(rawHash, secretKey);
        json.put("signature", signature);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject responseBody = new JSONObject(response.body());
        return responseBody.getString("payUrl");
    }

    public boolean verifySignature(Map<String, Object> momoParams, String receivedSignature) throws Exception {
        String rawHash = "accessKey=" + accessKey
                + "&amount=" + momoParams.get("amount")
                + "&extraData=" + momoParams.get("extraData")
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + momoParams.get("orderId")
                + "&orderInfo=" + momoParams.get("orderInfo")
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + momoParams.get("requestId")
                + "&requestType=" + momoParams.get("requestType");

        String calculatedSignature = hmacSHA256(rawHash, secretKey);
        return calculatedSignature.equals(receivedSignature);
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(bytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
