package com.hmg.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmg.model.dto.AddExpenseDTO;
import com.hmg.model.dto.FeePaymentDTO;
import com.hmg.model.dto.HomePaymentsDTO;
import com.hmg.model.dto.PayBillDTO;
import com.hmg.model.entities.*;
import com.hmg.model.enums.Notifications;
import com.hmg.model.user.HomeManagerUserDetails;
import com.hmg.service.implemetation.*;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    private final UserServiceImpl userServiceImpl;
    private final HomesGroupServiceImpl homesGroupServiceImpl;
    private final MonthServiceImpl monthServiceImpl;
    private final PaymentsServiceImpl paymentsServiceImpl;
    private final BillServiceImpl billServiceImpl;
    private final ExpenseServiceImpl expenseServiceImpl;
    private final LocalDate now;

    public CashierController(UserServiceImpl userServiceImpl, HomesGroupServiceImpl homesGroupServiceImpl, MonthServiceImpl monthServiceImpl, PaymentsServiceImpl paymentsServiceImpl, BillServiceImpl billServiceImpl, ExpenseServiceImpl expenseServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.homesGroupServiceImpl = homesGroupServiceImpl;
        this.monthServiceImpl = monthServiceImpl;
        this.paymentsServiceImpl = paymentsServiceImpl;
        this.billServiceImpl = billServiceImpl;
        this.expenseServiceImpl = expenseServiceImpl;
        this.now = LocalDate.now();
    }

    private boolean isAuthorized(long homesGroupId, long userId) {
        return this.userServiceImpl.isOwner(homesGroupId, userId);
    }

    @GetMapping("")
    public ModelAndView cashier(@AuthenticationPrincipal HomeManagerUserDetails user) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("cashier");

        modelAndView.addObject("user", this.userServiceImpl.getUserById(user.getId()));
        modelAndView.addObject("now", this.now);

        return modelAndView;
    }

    @GetMapping("/homesGroup{homesGroupId}/print")
    public ModelAndView printMonth(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/print_month");

            return monthModelAndView(homesGroupId, month, year, modelAndView);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}")
    public ModelAndView cashierHomesGroup(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/cashier_homes_group");

            return monthModelAndView(homesGroupId, month, year, modelAndView);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}/next-month")
    public ModelAndView nextMonth(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/cashier_homes_group");

            if (month == 12) {
                month = 1;
                year++;
            } else {
                month++;
            }

            return monthModelAndView(homesGroupId, month, year, modelAndView);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}/previous-month")
    public ModelAndView previousMonth(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year,
                                  @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/cashier_homes_group");

            if (month == 1) {
                month = 12;
                year--;
            } else {
                month--;
            }

            return monthModelAndView(homesGroupId, month, year, modelAndView);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private ModelAndView monthModelAndView(long homesGroupId, int month, int year, ModelAndView modelAndView) {

        HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);

        month = month > 12 || month < 1 ? 1 : month;

        //TODO -> to redirect to this.now?
        if (year <= homesGroup.getStartPeriod().getYear() || year > this.now.getYear()) {
            int startPeriodMonth = homesGroup.getStartPeriod().getMonthValue();
            month = Math.max(month, startPeriodMonth);
            year = homesGroup.getStartPeriod().getYear();

        } else if (year == now.getYear()) {
            month = Math.min(month, now.getMonthValue());
        }

        modelAndView.addObject("monthNumber", month);
        modelAndView.addObject("monthName", MonthsUtility.getMonthName(month));
        modelAndView.addObject("yearNumber", year);
        modelAndView.addObject("monthsList", this.monthServiceImpl.getMonthsList(year, this.now.getYear(), homesGroup.getStartPeriod()));
        modelAndView.addObject("now", this.now);
        modelAndView.addObject("homesGroup", homesGroup);

        Month currnetMonth = homesGroup.getMonthByDate(month, year);

        if (currnetMonth != null) {
            currnetMonth = this.monthServiceImpl.getMonthById(currnetMonth.getId());

            modelAndView.addObject("currentMonth", currnetMonth);
            modelAndView.addObject("previousMonth", currnetMonth.getPreviousMonth());
            modelAndView.addObject("years", homesGroup.getYears());
            modelAndView.addObject("unpaidBills", this.billServiceImpl.findUnpaidBillsByMonthId(currnetMonth.getId()));
        }

        return modelAndView;
    }

    @PostMapping("/homesGroup{homesGroupId}/create-month")
    public String createMonth(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) {

        if (isAuthorized(homesGroupId, user.getId())) {
            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);

            this.monthServiceImpl.setHomesToMonth(this.monthServiceImpl.createMonth(month, year, homesGroup, this.monthServiceImpl.getPreviousMonth(month, year, homesGroup)), homesGroup);

            redirectAttributes.addAttribute("month", month);
            redirectAttributes.addAttribute("year", year);
            redirectAttributes.addFlashAttribute("success", Notifications.CREATED_SUCCESSFULLY.getValue());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return "redirect:/cashier/homesGroup{homesGroupId}";
    }

    @GetMapping("/homesGroup{homesGroupId}/get-payment")
    @ResponseBody
    public Map<String, List<HomePaymentsDTO>> getPaymentsByHomeId(@PathVariable long homesGroupId, @RequestParam long monthId, @RequestParam long monthHomeId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            List<HomePaymentsDTO> paymentsByHome =
                    this.paymentsServiceImpl.getPaymentsByHome(this.monthServiceImpl.getMonthHomeOfMonthById(this.monthServiceImpl.getMonthById(monthId), monthHomeId));

            return Collections.singletonMap("response", paymentsByHome);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}/view-payment")
    @ResponseBody
    public Map<String, List<HomePaymentsDTO>> viewPaymentsByHomeId(@PathVariable long homesGroupId, @RequestParam long monthId, @RequestParam long monthHomeId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            List<HomePaymentsDTO> paymentsByHome =
                    this.paymentsServiceImpl.viewPaymentsByHome(this.monthServiceImpl.getMonthHomeOfMonthById(this.monthServiceImpl.getMonthById(monthId), monthHomeId));

            return Collections.singletonMap("response", paymentsByHome);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/homesGroup{homesGroupId}/pay")
    public String pay(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @RequestParam long monthHomeId,
                      @RequestParam(name = "data") String data, @RequestParam(name = "paidDate") String date, @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) throws Exception {

        if (isAuthorized(homesGroupId, user.getId())) {
            ObjectMapper objectMapper = new ObjectMapper();
            FeePaymentDTO[] feePaymentDTOS = objectMapper.readValue(data, FeePaymentDTO[].class);

            Month currentMonth = this.monthServiceImpl.getMonthByNumberAndYearAndHomesGroupId(month, year, homesGroupId);
            MonthHomes monthHomes = currentMonth.getHomeById(monthHomeId);

            LocalDate paidDate = LocalDate.parse(date);

            this.monthServiceImpl.setTotalPaymentForHome(currentMonth, monthHomes, this.paymentsServiceImpl.makePayments(monthHomes, feePaymentDTOS), paidDate);

            redirectAttributes.addFlashAttribute("success", Notifications.PAYMENT_SUCCESSFULLY.getValue());
            return "redirect:" + String.format("/cashier/homesGroup%d?month=%d&year=%d", homesGroupId, month, year);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @ModelAttribute("payBillDTO")
    public PayBillDTO payBillDTO() {
        return new PayBillDTO();
    }

    @PostMapping("/homesGroup{homesGroupId}/pay-bill")
    public String payBill(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @RequestParam long billId, @Valid PayBillDTO payBillDTO,
                          BindingResult bindingResult, @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) {
        if (isAuthorized(homesGroupId, user.getId())) {

            String redirectURL = "redirect:" + String.format("/cashier/homesGroup%d?month=%d&year=%d", homesGroupId, month, year);

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("payBillDTO", payBillDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "payBillDTO", bindingResult);

                return redirectURL;
            }

            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
            this.billServiceImpl.payBill(billId, payBillDTO, homesGroup.getMonthByDate(month, year));

            redirectAttributes.addFlashAttribute("success", Notifications.PAYMENT_SUCCESSFULLY.getValue());
            return redirectURL;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }



    @ModelAttribute("addExpenseDTO")
    public AddExpenseDTO addExpenseDTO() {
        return new AddExpenseDTO();
    }

    @PostMapping("/homesGroup{homesGroupId}/add-expense")
    public String addExpense(@PathVariable long homesGroupId, @RequestParam int month, @RequestParam int year, @Valid AddExpenseDTO addExpenseDTO, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            String redirectURL = "redirect:" + String.format("/cashier/homesGroup%d?month=%d&year=%d", homesGroupId, month, year);

            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("addExpenseDTO", addExpenseDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addExpenseDTO", bindingResult);

                return redirectURL;
            }

            Month currentMonth = this.monthServiceImpl.getMonthByNumberAndYearAndHomesGroupId(month, year, homesGroupId);
            currentMonth.setExpense(this.expenseServiceImpl.addExpenseToMonth(currentMonth, addExpenseDTO));

            this.monthServiceImpl.calculateTotalExpense(currentMonth);

            redirectAttributes.addFlashAttribute("success", Notifications.ADDED_SUCCESSFULLY.getValue());
            return redirectURL;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}/edit-expense{expenseId}")
    public ModelAndView editExpense(@PathVariable long homesGroupId, @PathVariable long expenseId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/edit-expense");
            modelAndView.addObject("expense", this.expenseServiceImpl.getExpenseById(expenseId));

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/homesGroup{homesGroupId}/edit-expense{expenseId}")
    public String editExpense(@PathVariable long homesGroupId, @PathVariable long expenseId, @Valid AddExpenseDTO addExpenseDTO, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {
            if(bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("addExpenseDTO", addExpenseDTO);
                redirectAttributes.addFlashAttribute(ConstantString.VALIDATION_PATH + "addExpenseDTO", bindingResult);

                return "redirect:/cashier/homesGroup{homesGroupId}/edit-expense{expenseId}";
            }

            Expense expense = this.expenseServiceImpl.editExpense(addExpenseDTO, expenseId);
            this.monthServiceImpl.calculateTotalExpense(expense.getMonth());

            redirectAttributes.addFlashAttribute("success", Notifications.UPDATED_SUCCESSFULLY.getValue());

            return "redirect:" + String.format("/cashier/homesGroup%d?month=%d&year=%d", homesGroupId, expense.getMonth().getNumber(), expense.getMonth().getYear());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/homesGroup{homesGroupId}/completeMonth")
    public String completeMonth(@PathVariable long homesGroupId, @RequestParam(name = "month") int month, @RequestParam(name = "year") int year,
                                @AuthenticationPrincipal HomeManagerUserDetails user, RedirectAttributes redirectAttributes) {

        String returnString = "redirect:" + String.format("/cashier/homesGroup%d?month=%d&year=%d", homesGroupId, month, year);

        if (isAuthorized(homesGroupId, user.getId())) {

            Month monthToComplete = this.monthServiceImpl.getMonthByNumberAndYearAndHomesGroupId(month, year, homesGroupId);

            if (this.monthServiceImpl.isCompleted(monthToComplete)) {
                this.monthServiceImpl.completeMonth(monthToComplete);
            } else {
                redirectAttributes.addFlashAttribute("fail", Notifications.MONTH_COMPLETE_NOT_ALL_PAYMENTS.getValue());
                return returnString;

            }

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        redirectAttributes.addFlashAttribute("success", Notifications.MONTH_COMPLETION.getValue());
        return returnString;
    }

    @GetMapping("/homesGroup{homesGroupId}/years")
    public ModelAndView getYears(@PathVariable long homesGroupId, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/years");

            HomesGroup homesGroup = this.homesGroupServiceImpl.getHomesGroupById(homesGroupId);
            modelAndView.addObject("homesGroup", homesGroup);
            modelAndView.addObject("years", this.monthServiceImpl.years(homesGroup.getYears(), homesGroupId));
            modelAndView.addObject("now", this.now);

            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/homesGroup{homesGroupId}/year{yearNumber}")
    public ModelAndView showYear(@PathVariable int homesGroupId, @PathVariable int yearNumber, @AuthenticationPrincipal HomeManagerUserDetails user) {

        if (isAuthorized(homesGroupId, user.getId())) {

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("cashier/showYear");
            modelAndView.addObject("homesGroup", this.homesGroupServiceImpl.getHomesGroupById(homesGroupId));
            modelAndView.addObject("year", this.monthServiceImpl.getYear(yearNumber, homesGroupId));
            modelAndView.addObject("now", this.now);


            return modelAndView;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}