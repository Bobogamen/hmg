package com.hmg.service;

import com.hmg.model.user.HomeManagerUserDetails;

public interface EmailService {

    void sendRegistrationEmail(String email);

    void sendRecoveryPasswordEmail(String email);

    void sendCashierRegistrationEmail(String email, HomeManagerUserDetails manager);

    void sendMessage(String email, String subject, String content);

    String generateEmailContent(String email, HomeManagerUserDetails manager, String content, String resetUrl, String appUrl);
}
