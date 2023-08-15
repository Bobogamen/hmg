package com.hmg.web;

import com.hmg.model.dto.AddHomeDTO;
import com.hmg.model.dto.AddHomesGroupDTO;
import com.hmg.model.dto.AddResidentDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Resident;
import com.hmg.model.enums.HomesGroupEnums;
import com.hmg.model.enums.Notifications;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.implemetation.HomeServiceImpl;
import com.hmg.service.implemetation.HomesGroupServiceImpl;
import com.hmg.service.implemetation.ResidentServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import com.hmg.utility.MonthsUtility;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping(value = "/profile")
public class HomesGroupController {

    private final UserServiceImpl userServiceImpl;
    private final HomesGroupServiceImpl homesGroupServiceImpl;
    private final HomeServiceImpl homeServiceImpl;
    private final ResidentServiceImpl residentServiceImpl;

    public HomesGroupController(UserServiceImpl userServiceImpl, HomesGroupServiceImpl homesGroupServiceImpl, HomeServiceImpl homeServiceImpl,
                                ResidentServiceImpl residentServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.homesGroupServiceImpl = homesGroupServiceImpl;
        this.homeServiceImpl = homeServiceImpl;
        this.residentServiceImpl = residentServiceImpl;
    }

    private boolean isAuthorized(long homesGroupId, long userId) {
        return this.userServiceImpl.isOwner(homesGroupId, userId);
    }

    @GetMapping("/homesGroup{homesGroupId}")
    ModelAndView viewHomesGroup(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/homes_group");

            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
            modelAndView.addObject("homesGroup", homesGroup);

            modelAndView.addObject("fees", homesGroup.getFees());
            modelAndView.addObject("bills", homesGroup.getBills());

            double total = homesGroup.getHomes().stream().mapToDouble(Home::getTotalForMonth).sum();
            modelAndView.addObject("total", total);

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/homesGroup{homesGroupId}/add-home")
    public ModelAndView addHome(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/add_home");
            modelAndView.addObject("homesGroup", this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));
            return modelAndView;

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @ModelAttribute("addHomeDTO")
    public AddHomeDTO addHomeDTO() {
        return new AddHomeDTO();
    }

    @ModelAttribute("addResidentDTO")
    public AddResidentDTO addResidentDTO() {
        return new AddResidentDTO();
    }

    @PostMapping("/homesGroup{homesGroupId}/add-home")
    public String addHome(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user,
                          @Valid AddHomeDTO addHomeDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (isAuthorized(homesGroupId, user.getId())) {

            boolean isResident = addHomeDTO.isResident();

            if (isResident) {
                boolean floor = bindingResult.hasFieldErrors("floor");
                boolean name = bindingResult.hasFieldErrors("name");
                boolean ownerFirstName = bindingResult.hasFieldErrors("ownerFirstName");

                if (floor || name || ownerFirstName) {
                    redirectAttributes.addFlashAttribute("addHomeDTO", addHomeDTO);
                    redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addHomeDTO", bindingResult);

                    return "redirect:/profile/homesGroup{homesGroupId}/add-home";
                }

            } else {
                if (bindingResult.hasErrors()) {
                    redirectAttributes.addFlashAttribute("addHomeDTO", addHomeDTO);
                    redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addHomeDTO", bindingResult);
                    redirectAttributes.addFlashAttribute("residentValue", false);

                    return "redirect:/profile/homesGroup{homesGroupId}/add-home";
                }
            }

            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
            if (homesGroup != null) {
                Home home = this.homeServiceImpl.addHome(addHomeDTO);
                Resident owner = this.residentServiceImpl.addOwner(addHomeDTO);
                this.homeServiceImpl.setOwnerToHome(home, owner);

                if (isResident) {
                    this.residentServiceImpl.addResidentToHome(owner, home);
                } else {
                    Resident resident = this.residentServiceImpl.addResident(addHomeDTO);
                    this.residentServiceImpl.addResidentToHome(resident, home);
                }

                this.homeServiceImpl.setHomeToHomesGroup(home, homesGroup);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            redirectAttributes.addFlashAttribute("success", Notifications.HOME_ADDED_SUCCESSFULLY.getValue());
            return "redirect:/profile/homesGroup{homesGroupId}";

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @ModelAttribute("allTypes")
    public List<String> allTypes() {
        return Arrays.stream(HomesGroupEnums.values()).map(HomesGroupEnums::getValue).toList();
    }


    @GetMapping("/homesGroup{homesGroupId}/edit")
    public ModelAndView editHomesGroup(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/edit-homes_group");

            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
            modelAndView.addObject("homesGroup", homesGroup);

            modelAndView.addObject("monthName", MonthsUtility.getMonthName(homesGroup.getStartPeriod().getMonthValue()));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @ModelAttribute("addHomesGroupDTO")
    public AddHomesGroupDTO addHomesGroupDTO() {
        return new AddHomesGroupDTO();
    }

    @PutMapping("/homesGroup{homesGroupId}/edit")
    public String editHomesGroup(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user,
                                 @Valid AddHomesGroupDTO addHomesGroupDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (isAuthorized(homesGroupId, user.getId())) {
            if (bindingResult.hasFieldErrors("name") || bindingResult.hasFieldErrors("size")) {
                redirectAttributes.addFlashAttribute("addHomesGroupDTO", addHomesGroupDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addHomesGroupDTO", bindingResult);

                return "redirect:/profile/homesGroup{homesGroupId}/edit";
            }

            this.homesGroupServiceImpl.editHomesGroup(addHomesGroupDTO, homesGroupId);

            redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());
            return "redirect:/profile/homesGroup{homesGroupId}";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
