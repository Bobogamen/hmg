package com.hmg.web;

import com.hmg.model.dto.AddResidentDTO;
import com.hmg.model.dto.EditHomeDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Resident;
import com.hmg.model.enums.Notifications;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.implemetation.HomeServiceImpl;
import com.hmg.service.implemetation.HomesGroupServiceImpl;
import com.hmg.service.implemetation.ResidentServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/profile/homesGroup{homesGroupId}/home{homeId}")
public class HomeController {

    private final UserServiceImpl userServiceImpl;
    private final HomesGroupServiceImpl homesGroupServiceImpl;
    private final HomeServiceImpl homeServiceImpl;
    private final ResidentServiceImpl residentServiceImpl;

    public HomeController(UserServiceImpl userServiceImpl, HomesGroupServiceImpl homesGroupServiceImpl, HomeServiceImpl homeServiceImpl, ResidentServiceImpl residentServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.homesGroupServiceImpl = homesGroupServiceImpl;
        this.homeServiceImpl = homeServiceImpl;
        this.residentServiceImpl = residentServiceImpl;
    }

    private boolean isAuthorized(long homeGroupId, long userId) {
        return this.userServiceImpl.isOwner(homeGroupId, userId);
    }

    @GetMapping("")
    public ModelAndView home(@PathVariable long homesGroupId, @PathVariable long homeId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            return getModelAndView(homesGroupId, homeId, "current");
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/next")
    public ModelAndView nextHome(@PathVariable long homesGroupId, @PathVariable long homeId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            return getModelAndView(homesGroupId, homeId, "next");
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/previous")
    public ModelAndView previousHome(@PathVariable long homesGroupId, @PathVariable long homeId, @AuthenticationPrincipal HomeManagerUserDetails user) {
        if (isAuthorized(homesGroupId, user.getId())) {
            return getModelAndView(homesGroupId, homeId, "previous");
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private ModelAndView getModelAndView(long homesGroupId, long homeId, String state) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("manager/home");

        HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
        modelAndView.addObject("homesGroup", homesGroup);

        if (state.equals("current")) {
            modelAndView.addObject("home", this.homeServiceImpl.getHomeById(homeId));

            return modelAndView;
        } else {
            if (state.equals("next")) {
                modelAndView.addObject("home", nextPreviousHomeFinder(homesGroup.getHomes(), homeId, true));
            } else if (state.equals("previous")) {
                modelAndView.addObject("home", nextPreviousHomeFinder(homesGroup.getHomes(), homeId, false));
            }
        }

        return modelAndView;
    }

    private Home nextPreviousHomeFinder(List<Home> homes, long currentHomeId, boolean next) {
        int nextIndex = 0;

        for (int i = 0; i < homes.size(); i++) {
            if (homes.get(i).getId() == currentHomeId) {
                nextIndex = i;
                break;
            }
        }

        if (next) {
            return nextIndex == homes.size() - 1 ? homes.get(0) : homes.get(nextIndex + 1);
        } else {
            return nextIndex == 0 ? homes.get(homes.size() - 1) : homes.get(nextIndex - 1);
        }
    }

    @ModelAttribute("editHomeDTO")
    public EditHomeDTO editHomeDTO() {
        return new EditHomeDTO();
    }

    @GetMapping("/edit")
    public ModelAndView editHome(@PathVariable long homesGroupId, @PathVariable long homeId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/edit-home");
            modelAndView.addObject("homesGroup", this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));
            modelAndView.addObject("home", this.homeServiceImpl.getHomeById(homeId));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/edit")
    public String editHome(@PathVariable long homesGroupId, @PathVariable long homeId, @Valid EditHomeDTO editHomeDTO, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("editHomeDTO", editHomeDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "editHomeDTO", bindingResult);

                return "redirect:/profile/homesGroup{homesGroupId}/home{homeId}/edit";
            } else {
                this.homeServiceImpl.editHome(this.homeServiceImpl.getHomeById(homeId), editHomeDTO);
                redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());

                return "redirect:/profile/homesGroup{homesGroupId}/home{homeId}";
            }

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/add-resident")
    public String addResident(@PathVariable long homesGroupId, @PathVariable long homeId, @Valid AddResidentDTO addResidentDTO,  BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("fail", Notifications.INVALID_DATA.getValue());

                return "redirect:/profile/homesGroup{homesGroupId}";
            }

            Home home = this.homeServiceImpl.getHomeById( homeId);
            if (home != null) {
                Resident resident = this.residentServiceImpl.addResident(addResidentDTO);
                this.residentServiceImpl.addResidentToHome(resident, home);
                redirectAttributes.addFlashAttribute("success", Notifications.RESIDENT_ADDED_SUCCESSFULLY.getValue());
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/profile/homesGroup{homesGroupId}";
    }

    @GetMapping("/edit-owner{residentId}")
    public ModelAndView editOwner(@PathVariable long homesGroupId, @PathVariable long homeId,
                                  @PathVariable long residentId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/edit_owner");
            modelAndView.addObject("owner", this.residentServiceImpl.getResidentById(residentId));
            modelAndView.addObject("home", this.homeServiceImpl.getHomeById(homeId));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/edit-resident{residentId}")
    public String editResident(@PathVariable long homesGroupId, @PathVariable long residentId, @PathVariable long homeId,
                               @RequestParam(name = "resident", required = false) String resident, @AuthenticationPrincipal HomeManagerUserDetails user,
                               AddResidentDTO addResidentDTO, RedirectAttributes redirectAttributes) {

        if (isAuthorized(homesGroupId, user.getId())) {
            Home home = this.homeServiceImpl.getHomeById(homeId);

            this.residentServiceImpl.editResident(addResidentDTO, residentId, home, resident != null || home.getOwner().getId() != residentId || home.getResidents().size() <= 1);

            redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/profile/homesGroup{homesGroupId}/home{homeId}";
    }

    @GetMapping("/delete-resident{residentId}")
    public ModelAndView deleteResident(@PathVariable long homesGroupId, @PathVariable long homeId, @PathVariable long residentId,
                                       @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/delete_resident");
            modelAndView.addObject("resident", this.residentServiceImpl.getResidentById(residentId));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/delete-resident{residentId}")
    public String deleteResident(@PathVariable long homesGroupId, @PathVariable long homeId, @PathVariable long residentId,
                                 @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) {

        if (isAuthorized(homesGroupId, user.getId())) {

            this.residentServiceImpl.deleteResidentById(residentId);

            redirectAttributes.addFlashAttribute("fail", Notifications.RESIDENT_DELETED_SUCCESSFULLY.getValue());

            return "redirect:/profile/homesGroup{homesGroupId}/home{homeId}";

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("/edit-fee-times{feeId}")
    public String editFee(@PathVariable long homesGroupId, @PathVariable long homeId, @PathVariable long feeId,
                          @RequestParam("times") int times, @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) {
        if (isAuthorized(homesGroupId, user.getId())) {
            this.homeServiceImpl.changeTimesOfFeeById(homeId, feeId, times);

            redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/profile/homesGroup{homesGroupId}/home{homeId}";
    }
}
