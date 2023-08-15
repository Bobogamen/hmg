package com.hmg.web;

import com.hmg.service.implemetation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userServiceImpl;
    private final HttpServletRequest request;


    public AdminController(UserServiceImpl userServiceImpl, HttpServletRequest request) {
        this.userServiceImpl = userServiceImpl;
        this.request = request;
    }

    @GetMapping("")
    public ModelAndView admin() {

        if (this.request.isUserInRole("ADMIN")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("admin/admin");
            modelAndView.addObject("allUser", this.userServiceImpl.getAllUsers());

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
