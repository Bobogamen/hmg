package com.hmg.model.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "homes")
public class Home implements Comparable<Home>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String floor;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double totalForMonth;

    @OneToOne(targetEntity = Resident.class)
    private Resident owner;

    @ManyToOne(targetEntity = HomesGroup.class)
    @JoinColumn(name = "homes_group_id", referencedColumnName = "id")
    private HomesGroup homesGroup;

    @OneToMany(mappedBy = "home", targetEntity = Resident.class, fetch = FetchType.EAGER)
    private List<Resident> residents;

    @OneToMany(mappedBy = "home", targetEntity = MonthHomes.class, fetch = FetchType.EAGER)
    private List<MonthHomes> months;

    @OneToMany(mappedBy = "home", targetEntity = HomesFee.class,fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HomesFee> fees;

    public Home() {
        this.residents = new ArrayList<>();
        this.months = new ArrayList<>();
        this.fees = new ArrayList<>();
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

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Resident getOwner() {
        return owner;
    }

    public void setOwner(Resident owner) {
        this.owner = owner;
    }

    public List<Resident> getResidents() {
        return this.residents;
    }

    public void addResidents(Resident resident) {
        this.residents.add(resident);
    }

    public List<MonthHomes> getMonths() {
        return months;
    }

    public void setMonths(MonthHomes months) {
        this.months.add(months);
    }

    public double getTotalForMonth() {
        return this.totalForMonth;
    }

    public void setTotalForMonth(double totalForMonth) {
        this.totalForMonth = totalForMonth;
    }

    public HomesGroup getHomesGroup() {
        return homesGroup;
    }

    public void setHomesGroup(HomesGroup homesGroup) {
        this.homesGroup = homesGroup;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }

    public void setMonths(List<MonthHomes> months) {
        this.months = months;
    }

    public List<HomesFee> getFees() {
        return fees.stream().toList();
    }

    public void addFee(HomesFee homesFee) {
        this.fees.add(homesFee);
    }

    public void setFees(List<HomesFee> fees) {
        this.fees.clear();
        this.fees = fees;
    }

    @Override
    public int compareTo(Home home) {
        return 0;
    }
}
