package com.hmg.model.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate registeredOn;

    @Column
    private String resetPasswordToken;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_homes_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "homes_group_id"))
    private List<HomesGroup> homesGroups;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "cashiers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "cashier_id"))
    private List<User> cashiers;

    public User() {
        this.roles = new ArrayList<>();
        this.homesGroups = new ArrayList<>();
        this.cashiers = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles.stream().toList();
    }

    public void setRoles(Role role) {
        this.roles.add(role);
    }

    public List<HomesGroup> getHomesGroups() {
        return homesGroups;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public void setRole(List<Role> role) {
        this.roles = role;
    }

    public void setHomesGroups(HomesGroup homesGroup) {
        this.homesGroups.add(homesGroup);
    }

    public List<User> getCashiers() {
        return cashiers;
    }

    public void setCashier(User cashier) {
        this.cashiers.add(cashier);
    }

    public boolean hasThisHomesGroup(HomesGroup homesGroup) {
        return this.homesGroups.stream().anyMatch(hg -> hg.getId() == homesGroup.getId());
    }

    public void removeHomesGroup(HomesGroup homesGroup) {
        this.homesGroups.remove(homesGroup);
    }
}
