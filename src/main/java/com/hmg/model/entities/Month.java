package com.hmg.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hmg.utility.MonthsUtility;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "months")
public class Month {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double currentIncome;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalExpenses;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalBills;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double currentDifference;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalDifference;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double previousMonthDifference;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BOOLEAN")
    private boolean completed;

    @OneToOne(targetEntity = Month.class)
    private Month previousMonth;

    @ManyToOne(targetEntity = HomesGroup.class)
    @JoinColumn(name = "homes_group_id", referencedColumnName = "id")
    private HomesGroup homesGroup;

    @OneToMany(mappedBy = "month", targetEntity = MonthHomes.class)
    private List<MonthHomes> homes;

    @OneToMany(mappedBy = "month", targetEntity = Expense.class)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "month", targetEntity = BillsPaid.class)
    private List<BillsPaid> billsPaid;

    public Month() {
        this.homes = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.billsPaid = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMonthName(int number) {
        return MonthsUtility.getMonthName(number);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Month getPreviousMonth() {
        return previousMonth;
    }

    public void setPreviousMonth(Month previousMonth) {
        this.previousMonth = previousMonth;
    }

    public double getCurrentIncome() {
        return currentIncome;
    }

    public void setCurrentIncome(double currentIncome) {
        this.currentIncome = currentIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(double totalBills) {
        this.totalBills = totalBills;
    }

    public double getCurrentDifference() {
        return currentDifference;
    }

    public void setCurrentDifference(double currentDifference) {
        this.currentDifference = currentDifference;
    }

    public double getTotalDifference() {
        return totalDifference;
    }

    public void setTotalDifference(double totalDifference) {
        this.totalDifference = totalDifference;
    }

    public double getPreviousMonthDifference() {
        return previousMonthDifference;
    }

    public void setPreviousMonthDifference(double previousMonthDifference) {
        this.previousMonthDifference = previousMonthDifference;
    }

    public HomesGroup getHomesGroup() {
        return homesGroup;
    }

    public void setHomesGroup(HomesGroup homesGroup) {
        this.homesGroup = homesGroup;
    }

    public List<MonthHomes> getHomes() {
        return homes;
    }

    public void setHomes(List<MonthHomes> homes) {
        this.homes = homes;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpense(Expense expense) {
        this.expenses.add(expense);
    }

    public List<BillsPaid> getBillsPaid() {
        return billsPaid;
    }

    public void setBillsPaid(List<BillsPaid> billsPaid) {
        this.billsPaid = billsPaid;
    }

    public MonthHomes getHomeById(long monthHomeId) {
        return this.homes.stream().filter(h -> h.getId() == monthHomeId).iterator().next();
    }
}
