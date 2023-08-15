package com.hmg.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate addedOn;

    @ManyToOne(targetEntity = HomesGroup.class)
    @JoinColumn(name = "homes_group_id", referencedColumnName = "id")
    private HomesGroup homesGroup;

    @OneToMany(mappedBy = "bill", targetEntity = BillsPaid.class)
    private List<BillsPaid> billsPaid;

    public Bill() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(LocalDate addedOn) {
        this.addedOn = addedOn;
    }

    public HomesGroup getHomesGroup() {
        return homesGroup;
    }

    public void setHomesGroup(HomesGroup homesGroup) {
        this.homesGroup = homesGroup;
    }

    public List<BillsPaid> getBillsPaid() {
        return this.billsPaid;
    }

    public void setBillsPaid(BillsPaid billsPaid) {
        this.billsPaid.add(billsPaid);
    }
}
