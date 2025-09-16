/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.controllers;

import com.tba.components.MomoConfig;
import com.tba.components.VNPayConfig;
import com.tba.dto.request.TransactionRequestDTO;
import com.tba.enums.PaymentStatus;
import com.tba.pojo.ServicePost;
import com.tba.pojo.Transaction;
import com.tba.pojo.User;
import com.tba.services.ServicePostService;
import com.tba.services.TransactionService;
import com.tba.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api")
public class ApiTransactionController {

    @Autowired
    private UserService userService;
    @Autowired
    private ServicePostService servicePostService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private VNPayConfig vnPayConfig;
    @Autowired
    private MomoConfig momoConfig;

    @PostMapping("/secure/transaction/service-post/{postId}")
    public ResponseEntity<?> createTransaction(
            @PathVariable("postId") Integer postId,
            @RequestBody TransactionRequestDTO dto,
            Principal principal,
            HttpServletRequest req) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập!");
        }

        ServicePost post = servicePostService.getServicePostById(postId);
        if (post == null || post.getIsDeleted()) {
            return ResponseEntity.badRequest().body("Dịch vụ không tồn tại!");
        }

        if (dto.getSlotQuantity() <= 0 || dto.getSlotQuantity() > post.getAvailableSlot()) {
            return ResponseEntity.badRequest().body("Số lượng slot không hợp lệ!");
        }

        BigDecimal total = post.getPrice().multiply(BigDecimal.valueOf(dto.getSlotQuantity()));

        // Tạo transaction code duy nhất
        String transactionCode = "TXN" + System.currentTimeMillis();

        Transaction tx = new Transaction();
        tx.setTransactionCode(transactionCode);
        tx.setServicePostId(post);
        tx.setUserId(user);
        tx.setSlotQuantity(dto.getSlotQuantity());
        tx.setTransactionType(dto.getTransactionType());
        tx.setTotalAmount(total);
        tx.setPaymentStatus(PaymentStatus.UNPAID);
        tx.setCreatedDate(new Date());
        tx.setUpdatedDate(new Date());
        transactionService.add(tx);

        String payUrl = null;
        try {
            switch (dto.getTransactionType().name()) {
                case "VNPAY":
                    String clientIp = req.getRemoteAddr();
                    ;
                    payUrl = vnPayConfig.createVNPayUrl(
                            total,
                            "Thanh toán dịch vụ: " + post.getName(),
                            clientIp,
                            transactionCode
                    );
                    break;
                case "MOMO":
                    payUrl = momoConfig.createMoMoPaymentUrl(
                            total.longValue(),
                            transactionCode,
                            "Thanh toán dịch vụ: " + post.getName()
                    );
                    break;
                default:
                    return ResponseEntity.badRequest().body("Loại thanh toán không hợp lệ!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi tạo URL thanh toán!");
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "transactionId", tx.getId(),
                "transactionCode", transactionCode,
                "amount", total,
                "paymentStatus", tx.getPaymentStatus().name(),
                "payUrl", payUrl,
                "message", "Tạo giao dịch thành công, chuyển hướng đến cổng thanh toán!"
        ));
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> params,
            HttpServletResponse response) throws IOException {
        String txnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpSecureHash = params.get("vnp_SecureHash");

        String status = "FAILED";
        try {
            boolean valid = vnPayConfig.verifySignature(params, vnpSecureHash);

            if (valid) {
                Transaction tx = transactionService.getTransactionByTransactionCode(txnRef);
                if (tx != null) {
                    if ("00".equals(vnpResponseCode)) {
                        tx.setPaymentStatus(PaymentStatus.PAID);
                        status = "SUCCESS";

                        ServicePost sp = servicePostService.getServicePostById(tx.getServicePostId().getId());
                        sp.setAvailableSlot(sp.getAvailableSlot() - tx.getSlotQuantity());
                        transactionService.update(sp, tx);
                    } else {
                        tx.setPaymentStatus(PaymentStatus.FAILED);
                        transactionService.update(null, tx);
                    }
                    tx.setUpdatedDate(new Date());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // log lỗi server
            status = "FAILED";   // fallback
        }

        // redirect về frontend trong mọi trường hợp
        String redirectUrl = String.format(
                "http://localhost:3000/payment-return?transactionCode=%s&status=%s",
                txnRef != null ? txnRef : "UNKNOWN", status
        );
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/momo-return")
    public ResponseEntity<?> momoReturn(@RequestBody Map<String, Object> payload) {
        try {
            String orderId = (String) payload.get("orderId");
            Transaction tx = transactionService.getTransactionByTransactionCode(orderId);
            if (tx == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy transaction");
            }

            int resultCode = Integer.parseInt(payload.get("resultCode").toString());
            tx.setUpdatedDate(new Date());

            if (resultCode == 0) {
                tx.setPaymentStatus(PaymentStatus.PAID);
                // chỉ trừ slot khi thanh toán thành công
                ServicePost sp = servicePostService.getServicePostById(tx.getServicePostId().getId());
                sp.setAvailableSlot(sp.getAvailableSlot() - tx.getSlotQuantity());
                transactionService.update(sp, tx);
            } else {
                tx.setPaymentStatus(PaymentStatus.FAILED);
                transactionService.update(null, tx);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Received IPN",
                    "success", true,
                    "transactionCode", orderId,
                    "paymentStatus", tx.getPaymentStatus()
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi xử lý callback MoMo");
        }
    }
}
