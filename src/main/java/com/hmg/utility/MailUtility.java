package com.hmg.utility;

import com.hmg.model.enums.Mail;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;


public class MailUtility {
    public static String appUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.replace(request.getServletPath(), "");
    }

    public static String passwordResetMailSubject() {
        return Mail.hmg_FORGOTTEN_PASSWORD.getValue();
    }

    public static String registrationMailSubject() {
        return Mail.hmg_WELCOME.getValue();
    }

    public static String resetPasswordUrl(HttpServletRequest request, String token) {
        return MailUtility.appUrl(request) + "/reset-password?token=" + token;
    }

    public static String getToken() {
        return RandomString.make(30);
    }
}
