/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.services.impl;

import com.tba.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Admin
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromAddress;

    private static final String FOOTER = "\n\nTrân trọng,\nĐông Thành Tour Booking";

    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text + FOOTER);
        message.setFrom(fromAddress); // Gmail của bạn
        mailSender.send(message);
    }

    @Override
    public void sendProviderPending(String to, String companyName) {
        String subject = "Đăng ký tài khoản nhà cung cấp - Chờ duyệt";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Tài khoản nhà cung cấp của bạn đã được tạo và đang trong trạng thái chờ duyệt.\n"
                + "Bạn sẽ nhận thông báo tiếp theo khi quản trị viên xử lý yêu cầu.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendProviderApproved(String to, String companyName) {
        String subject = "Duyệt tài khoản nhà cung cấp thành công";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Tài khoản nhà cung cấp của bạn đã được duyệt thành công.\n"
                + "Bạn có thể đăng nhập và sử dụng đầy đủ chức năng của hệ thống.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendProviderRejected(String to, String companyName) {
        String subject = "Từ chối duyệt tài khoản nhà cung cấp";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Rất tiếc, yêu cầu đăng ký tài khoản nhà cung cấp của bạn đã bị từ chối.\n"
                + "Vui lòng liên hệ quản trị viên nếu cần thêm thông tin.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendProviderDisabled(String to, String companyName) {
        String subject = "Tài khoản nhà cung cấp đã bị vô hiệu hóa";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Tài khoản nhà cung cấp của bạn đã bị vô hiệu hóa bởi quản trị viên.\n"
                + "Vui lòng liên hệ quản trị viên để biết thêm chi tiết.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendPermissionApproved(String to, String companyName, String serviceType) {
        String subject = "Duyệt quyền dịch vụ thành công";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Yêu cầu cấp quyền dịch vụ [" + serviceType + "] của bạn đã được duyệt.\n"
                + "Bạn có thể đăng tải và quản lý dịch vụ này.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendPermissionRejected(String to, String companyName, String serviceType) {
        String subject = "Từ chối quyền dịch vụ";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Yêu cầu cấp quyền dịch vụ [" + serviceType + "] của bạn đã bị từ chối.\n"
                + "Vui lòng liên hệ quản trị viên nếu cần thêm thông tin.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendPermissionDisabled(String to, String companyName, String serviceType) {
        String subject = "Quyền dịch vụ đã bị vô hiệu hóa";
        String body = "Xin chào" + companyName + ",\n\n"
                + "Quyền dịch vụ [ " + serviceType + "] của bạn đã bị vô hiệu hóa bởi quản trị viên.\n"
                + "Vui lòng liên hệ quản trị viên để biết thêm chi tiết.";
        sendMail(to, subject, body);
    }

    @Override
    public void sendPermissionPending(String to, String companyName, String serviceType) {
        String subject = "Yêu cầu quyền dịch vụ đang chờ duyệt";
        String body = "Xin chào " + companyName + ",\n\n"
                + "Yêu cầu cấp quyền dịch vụ [" + serviceType + "] của bạn đã được gửi và đang chờ quản trị viên duyệt.\n"
                + "Bạn sẽ nhận thông báo khi có kết quả.";
        sendMail(to, subject, body);
    }
}
