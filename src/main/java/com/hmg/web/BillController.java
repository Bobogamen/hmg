package com.hmg.web;

import com.hmg.model.dto.AddBillDTO;
import com.hmg.model.enums.Notifications;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.implemetation.BillServiceImpl;
import com.hmg.service.implemetation.HomesGroupServiceImpl;
import com.hmg.service.implemetation.UserServiceImpl;
import com.hmg.utility.ConstantString;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile/homesGroup{homesGroupId}")
public class BillController {

    private final UserServiceImpl userServiceImpl;
    private final HomesGroupServiceImpl homesGroupServiceImpl;
    private final BillServiceImpl billServiceImpl;


    public BillController(UserServiceImpl userServiceImpl, HomesGroupServiceImpl homesGroupServiceImpl, BillServiceImpl billServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.homesGroupServiceImpl = homesGroupServiceImpl;
        this.billServiceImpl = billServiceImpl;
    }

    private boolean isAuthorized(long homeGroupId, long userId) {
        return this.userServiceImpl.isOwner(homeGroupId, userId);
    }

    @GetMapping("/add-bill")
    public ModelAndView addFee(@PathVariable long homesGroupId) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("manager/add-bill");

        modelAndView.addObject("homesGroup", this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));

        return modelAndView;
    }

    @ModelAttribute("addBillDTO")
    public AddBillDTO addBillDTO() {
        return new AddBillDTO();
    }

    @PostMapping("/add-bill")
    public String addBill(@PathVariable long homesGroupId, @Valid AddBillDTO addBillDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("addBillDTO", addBillDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addBillDTO", bindingResult);

                return "redirect:/profile/homesGroup{homesGroupId}/add-bill";
            }

            this.billServiceImpl.addBill(addBillDTO, this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));


            redirectAttributes.addFlashAttribute("success");

            return "redirect:/profile/homesGroup{homesGroupId}";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/edit-bill{billId}")
    public ModelAndView editBill(@PathVariable long homesGroupId, @PathVariable long billId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("manager/edit-bill");

            modelAndView.addObject("homesGroup", this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));
            modelAndView.addObject("bill", this.billServiceImpl.getBillById(billId));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/edit-bill{billId}")
    public String editBill(@PathVariable long homesGroupId, @PathVariable long billId, @Valid AddBillDTO addBillDTO, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            if (bindingResult.hasFieldErrors("name")) {
                redirectAttributes.addFlashAttribute("addBillDTO", addBillDTO);
                redirectAttributes.addFlashAttribute( ConstantString.VALIDATION_PATH + "addBillDTO", bindingResult);

                return "redirect:/profile/homesGroup{homesGroupId}/edit-bill" + billId;
            }

            this.billServiceImpl.editBill(addBillDTO.getName(), this.billServiceImpl.getBillById(billId));

            redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());
            return "redirect:/profile/homesGroup{homesGroupId}";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}

