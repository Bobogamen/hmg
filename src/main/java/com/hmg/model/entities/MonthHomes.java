package com.hmg.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "month_homes")
public class MonthHomes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalDate paidDate;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalPaid;

    @ManyToOne(targetEntity = Month.class)
    @JoinColumn(name = "month_id", referencedColumnName = "id")
    private Month month;

    @ManyToOne(targetEntity = Home.class)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private Home home;

    @OneToMany(mappedBy = "monthHomes", targetEntity = Payment.class)
    private List<Payment> payments;

    public MonthHomes() {
        this.payments = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Payment payment) {
        this.payments.add(payment);
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }
}
