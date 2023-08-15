package com.hmg.service.implemetation;

import com.hmg.config.mail.MailProperties;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.EmailService;
import com.hmg.utility.MailUtility;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final UserServiceImpl userServiceImpl;
    private final MailProperties mailProperties;
    private final TemplateEngine templateEngine;
    private final HttpServletRequest request;

    public EmailServiceImpl(JavaMailSender javaMailSender, UserServiceImpl userServiceImpl, MailProperties mailProperties, TemplateEngine templateEngine, HttpServletRequest request) {
        this.javaMailSender = javaMailSender;
        this.userServiceImpl = userServiceImpl;
        this.mailProperties = mailProperties;
        this.templateEngine = templateEngine;
        this.request = request;
    }

    @Override
    public void sendRegistrationEmail(String email) {
        String subject = MailUtility.registrationMailSubject();
        String content = generateEmailContent(email, null, "registration", null, MailUtility.appUrl(request));

        sendMessage(email, subject, content);
    }

    @Override
    public void sendRecoveryPasswordEmail(String email) {

        String subject = MailUtility.passwordResetMailSubject();
        String resetPasswordUrl = MailUtility.resetPasswordUrl(request, this.userServiceImpl.getResetPasswordTokenByEmail(email));
        String content = generateEmailContent(email, null, "forgot_password", resetPasswordUrl, MailUtility.appUrl(request));

        sendMessage(email, subject, content);
    }

    @Override
    public void sendCashierRegistrationEmail(String email, HomeManagerUserDetails manager) {
        String subject = MailUtility.registrationMailSubject();
        String content = generateEmailContent(email, manager, "cashier", null, MailUtility.appUrl(request));

        sendMessage(email, subject, content);
    }

    @Override
    public void sendMessage(String email, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name());

            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);

            //--------LOGO PART--------
            Path logo = Paths.get("src/main/resources/static/images/home-page/logo.png");
            FileSystemResource fileSystemResourceLogo = new FileSystemResource(logo);
            messageHelper.addInline("logo", fileSystemResourceLogo);

            javaMailSender.send(messageHelper.getMimeMessage());

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String generateEmailContent(String email, HomeManagerUserDetails manager, String content, String resetUrl, String appUrl) {

        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("reset_url", resetUrl);
        context.setVariable("app_url", appUrl);

        if (manager != null) {
            context.setVariable("manager_name", manager.getName());
            context.setVariable("manager_email", manager.getEmail());
        }

        if (content.equals("registration")) {
            return this.templateEngine.process("email/registration", context);
        } else if (content.equals("cashier")){
            return this.templateEngine.process("email/cashier_registration", context);
        } else {
            return this.templateEngine.process("email/forgot_password", context);
        }
    }
}
