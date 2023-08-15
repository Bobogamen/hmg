package com.hmg.web;

import com.hmg.model.dto.RegistrationDTO;
import com.hmg.model.enums.Notifications;
import com.hmg.service.implemetation.EmailServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginRegisterController {

    private final UserServiceImpl userServiceImpl;
    private final EmailServiceImpl emailServiceImpl;

    public LoginRegisterController(UserServiceImpl userServiceImpl, EmailServiceImpl emailServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.emailServiceImpl = emailServiceImpl;
    }

    //-------------------- LOGIN FAIL SECTION START --------------------
    @PostMapping("/login-fail")
    public String loginFail(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String userName,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, userName);
        redirectAttributes.addFlashAttribute("fail", Notifications.INVALID_LOGIN.getValue());

        return "redirect:/";
    }
    //-------------------- LOGIN FAIL SECTION END --------------------

    @GetMapping("/register")
    public ModelAndView register() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @ModelAttribute("registrationDTO")
    public RegistrationDTO registrationDTO() {
        return new RegistrationDTO();
    }

    @Transactional
    @PostMapping("/register")
    public String register(@Valid RegistrationDTO registrationDTO,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) throws ServletException {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "registrationDTO", bindingResult);

            return "redirect:/register";
        }

        this.userServiceImpl.register(registrationDTO);
        System.out.printf("%s is registered successfully%n", registrationDTO.getEmail());
        this.emailServiceImpl.sendRegistrationEmail(registrationDTO.getEmail());

        redirectAttributes.addFlashAttribute("success", Notifications.REGISTRATION_SUCCESSFULLY.getValue());

        return "redirect:/profile";
    }
}
