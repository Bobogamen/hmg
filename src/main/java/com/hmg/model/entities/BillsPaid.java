package com.hmg.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bills_paid")
public class BillsPaid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate paidOn;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double value;

    @Column(nullable = false)
    private String documentNumber;

    @Column(nullable = false)
    private LocalDate documentDate;

    @ManyToOne(targetEntity = Bill.class)
    @JoinColumn(name = "bill_id", referencedColumnName = "id")
    private Bill bill;

    @ManyToOne(targetEntity = Month.class)
    @JoinColumn(name = "month_id", referencedColumnName = "id")
    private Month month;

    public BillsPaid() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDate addedOn) {
        this.paidOn = addedOn;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }
}
