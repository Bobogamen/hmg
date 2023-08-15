package com.hmg.service;

import com.hmg.model.dto.YearDTO;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Month;
import com.hmg.model.entities.MonthHomes;
import com.hmg.model.enums.Months;

import java.time.LocalDate;
import java.util.List;

public interface MonthService {
    Month getMonthById(long monthId);

    Month createMonth(int month, int year, HomesGroup homesGroup, Month previousMonth);

    void setHomesToMonth(Month newMonth, HomesGroup homesGroup);

    MonthHomes getMonthHomeOfMonthById(Month month, long monthHomeId);

    Month getMonthByNumberAndYearAndHomesGroupId(int month, int year, long homesGroupId);

    boolean isCompleted(Month month);

    void setTotalPaymentForHome(Month month, MonthHomes monthHome, double totalPaid, LocalDate paidDate);

    void calculateTotalExpense(Month month);

    YearDTO getYear(int yearNumber, long homesGroupId);

    List<YearDTO> years(List<Integer> yearsList, long homesGroupId);

    void completeMonth(Month month);

    Months[] getMonthsValuesCurrentYear();

    Months[] getMonthsValuesStartPeriodYear(int monthValue);

    Months[] getMonthsList(int year, int currentYear, LocalDate homesGroupStartPeriod);

    void initialMonthsGeneration(HomesGroup homesGroup);

    Month getPreviousMonth(int month, int year, HomesGroup homesGroup);

    void calculateMonth(Month month);

    double getCurrentIncomeOfMonth(Month month);

    double getTotalExpensesOfMonth(Month month);
}
