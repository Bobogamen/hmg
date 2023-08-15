package com.hmg.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "homes_fee")
public class HomesFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int times;

    @ManyToOne(targetEntity = Fee.class)
    @JoinColumn(name = "fee_id", referencedColumnName = "id")
    private Fee fee;

    @ManyToOne(targetEntity = Home.class)
    @JoinColumn(name = "home_id", referencedColumnName = "id")
    private Home home;

    public HomesFee() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
