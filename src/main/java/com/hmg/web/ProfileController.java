package com.hmg.web;

import com.hmg.model.dto.AddHomesGroupDTO;
import com.hmg.model.dto.RegistrationDTO;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.enums.Notifications;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.implemetation.EmailServiceImpl;
import com.hmg.service.implemetation.HomesGroupServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserServiceImpl userServiceImpl;
    private final HomesGroupServiceImpl homesGroupServiceImpl;
    private final HttpServletRequest request;
    private final EmailServiceImpl emailServiceImpl;

    public ProfileController(UserServiceImpl userServiceImpl, HomesGroupServiceImpl homesGroupServiceImpl, EmailServiceImpl emailServiceImpl, HttpServletRequest request) {
        this.userServiceImpl = userServiceImpl;
        this.homesGroupServiceImpl = homesGroupServiceImpl;
        this.emailServiceImpl = emailServiceImpl;
        this.request = request;
    }

    @GetMapping("")
    public ModelAndView profile(@AuthenticationPrincipal HomeManagerUserDetails user) {

        if (this.request.isUserInRole("MANAGER")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile");

            modelAndView.addObject("homeGroups", this.userServiceImpl.getUserById(user.getId()).getHomesGroups());

            return modelAndView;
        } else {
            return new ModelAndView("redirect:/cashier");
        }
    }

    @GetMapping("/add-homes_group")
    public ModelAndView addHomesGroup() {

        if (this.request.isUserInRole("MANAGER")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/add-homes_group");
            return modelAndView;

        } else {
            return new ModelAndView("redirect:/cashier");
        }
    }

    @ModelAttribute("addHomesGroupDTO")
    public AddHomesGroupDTO addHomesGroupDTO() {
        return new AddHomesGroupDTO();
    }

    @PostMapping("/add-homes_group")
    public String addHomesGroup(@AuthenticationPrincipal HomeManagerUserDetails user,
                                 @Valid AddHomesGroupDTO addHomesGroupDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (this.request.isUserInRole("MANAGER")) {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("addHomesGroupDTO", addHomesGroupDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addHomesGroupDTO", bindingResult);

                return "redirect:/profile/add-homes_group";
            }

            HomesGroup newHomesGroup = this.homesGroupServiceImpl.addHomesGroup(addHomesGroupDTO);
            this.userServiceImpl.addHomesGroupToUser(user.getId(), newHomesGroup);

            redirectAttributes.addFlashAttribute("success", "Група " + addHomesGroupDTO.getName() + " e добавена");

            return "redirect:/profile";
        } else {
            return "redirect:/cashier";
        }
    }

    @GetMapping("/register-cashier")
    public ModelAndView registerCashier() {
        if (this.request.isUserInRole("MANAGER")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register");
            return modelAndView;

        } else {
            return new ModelAndView("redirect:/cashier");
        }
    }

    @ModelAttribute("registrationDTO")
    public RegistrationDTO registrationDTO() {
        return new RegistrationDTO();
    }

    @PostMapping("/register-cashier")
    @Transactional
    public String register(@AuthenticationPrincipal HomeManagerUserDetails user, @Valid RegistrationDTO registrationDTO, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if (this.request.isUserInRole("MANAGER")) {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "registrationDTO", bindingResult);

                return "redirect:/profile/register-cashier";
            }

            this.userServiceImpl.registerCashier(registrationDTO, user.getId());
            this.emailServiceImpl.sendCashierRegistrationEmail(registrationDTO.getEmail(), user);

            redirectAttributes.addFlashAttribute("success", Notifications.CASHIER_REGISTRATION_SUCCESSFULLY.getValue());
        }

        return "redirect:/cashier";
    }

    @GetMapping("/assign")
    public ModelAndView assign(@AuthenticationPrincipal HomeManagerUserDetails user) {

        if (this.request.isUserInRole("MANAGER")) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/assign");
            modelAndView.addObject("manager", this.userServiceImpl.getUserById(user.getId()));

            return modelAndView;
        } else {
            return new ModelAndView("redirect:/cashier");
        }
    }

    @PostMapping("/assign{cashierId}")
    public String assign(@PathVariable long cashierId, @AuthenticationPrincipal HomeManagerUserDetails user,
                         @RequestParam(name = "homesGroup", required = false) List<Long> homesGroupsIds, RedirectAttributes redirectAttributes) {

        if (this.request.isUserInRole("MANAGER")) {

            List<HomesGroup> homesGroups = new ArrayList<>();

            if (homesGroupsIds != null) {
                homesGroups = homesGroupsIds.stream().map(this.homesGroupServiceImpl::getHomesGroupById).toList();
            }

            this.userServiceImpl.setHomesGroupsToUser(homesGroups, cashierId);

            if (homesGroups.isEmpty()) {
                redirectAttributes.addFlashAttribute("fail", Notifications.CASHIER_NO_HOMES_GROUP.getValue());
            } else {
                redirectAttributes.addFlashAttribute("success", Notifications.ASSIGN_SUCCESSFULLY.getValue());
            }

            return "redirect:/profile/assign";

        } else {
            return "redirect:/cashier";
        }
    }

    @GetMapping("/edit-name")
    public ModelAndView editName(@AuthenticationPrincipal HomeManagerUserDetails user) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", this.userServiceImpl.getUserById(user.getId()));
        modelAndView.setViewName("edit_name");

        return modelAndView;
    }

    @PostMapping("/edit-name")
    public String editName(@Valid RegistrationDTO registrationDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (bindingResult.hasFieldErrors("name")) {
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "registrationDTO", bindingResult);

            return "redirect:/profile/edit-name";
        }

        this.userServiceImpl.editUserName(user.getId(), registrationDTO.getName());
        user.setName(registrationDTO.getName());

        redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());

        if (user.isCashier()) {
            return "redirect:/cashier";
        }

        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public ModelAndView changePassword(@AuthenticationPrincipal HomeManagerUserDetails user) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", this.userServiceImpl.getUserById(user.getId()));
        modelAndView.setViewName("change_password");

        return modelAndView;
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid RegistrationDTO registrationDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (bindingResult.hasFieldErrors("password") || bindingResult.hasFieldErrors("confirmPassword")) {
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "registrationDTO", bindingResult);

            return "redirect:/profile/change-password";
        }

        this.userServiceImpl.changePasswordByUserId(user.getId(), registrationDTO.getPassword());
        redirectAttributes.addFlashAttribute("success", Notifications.PASSWORD_CHANGED_SUCCESSFULLY.getValue());

        if (user.isCashier()) {
            return "redirect:/cashier";
        }

        return "redirect:/profile";
    }

    @GetMapping("/info")
    public ModelAndView info(@AuthenticationPrincipal HomeManagerUserDetails user) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", this.userServiceImpl.getUserById(user.getId()));
        modelAndView.setViewName("info");

        return modelAndView;
    }
}
