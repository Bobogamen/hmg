package com.hmg.web;

import com.hmg.model.dto.PasswordResetDTO;
import com.hmg.model.enums.Notifications;
import com.hmg.service.implemetation.EmailServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import com.hmg.utility.MailUtility;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotAndResetPasswordController {

    private final EmailServiceImpl emailServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ForgotAndResetPasswordController(EmailServiceImpl emailServiceImpl, UserServiceImpl userServiceImpl) {
        this.emailServiceImpl = emailServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(String email, RedirectAttributes redirectAttributes) {
        boolean found = this.userServiceImpl.findEmail(email);

        if (found) {
            this.userServiceImpl.updateResetPasswordToken(MailUtility.getToken(), email);

            try {
                this.emailServiceImpl.sendRecoveryPasswordEmail(email);
                System.out.printf("%s request a password reset%n", email);
            } catch (Exception exception) {
                System.out.println("Fail to sent password reset email");
                throw new RuntimeException(exception.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("foundEmail", found);
        redirectAttributes.addFlashAttribute("email", email);
        redirectAttributes.addFlashAttribute("show", true);

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPassword(@RequestParam String token) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reset_password");
        modelAndView.addObject("token", token);

        return modelAndView;
    }

    @ModelAttribute("passwordResetDTO")
    public PasswordResetDTO passwordResetDTO() {
        return new PasswordResetDTO();
    }

    @PostMapping("/reset-password")
    // !!! BindingResult MUST BE IMMEDIATELY AFTER @Valid ELEMENT OR DOESN'T WORK !!! --------------------
    public String resetPassword(@RequestParam("token") String token,
                                @Valid PasswordResetDTO passwordResetDTO,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        boolean foundEmailByToken = this.userServiceImpl.findEmailByToken(token);

        if (!foundEmailByToken) {
            redirectAttributes.addFlashAttribute("fail", Notifications.INVALID_EMAIL.getValue());
            return "redirect:/";

        } else if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("passwordResetDTO", passwordResetDTO);
            redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "passwordResetDTO", bindingResult);
            return "redirect:/reset-password?token=" + token;
        }

        String password = passwordResetDTO.getPassword();
        this.userServiceImpl.changePasswordOnEmailByToken(token, password);

        redirectAttributes.addFlashAttribute("success", Notifications.PASSWORD_CHANGED_SUCCESSFULLY.getValue());
        return "redirect:/";
    }
}
