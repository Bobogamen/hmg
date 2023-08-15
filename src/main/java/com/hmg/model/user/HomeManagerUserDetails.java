package com.hmg.model.user;

import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class HomeManagerUserDetails implements UserDetails {

    private long id;
    private String username;
    private String name;
    private String password;
    private LocalDate registeredOn;
    private Collection<GrantedAuthority> authorities;
    private final List<HomesGroup> homesGroups;
    private final List<User> cashiers;

    public HomeManagerUserDetails(long id,
                                  String email,
                                  String name,
                                  String password,
                                  LocalDate registeredOn,
                                  Collection<GrantedAuthority> authorities,
                                  List<HomesGroup> homesGroups, List<User> cashiers) {
        this.id = id;
        this.username = email;
        this.name = name;
        this.password = password;
        this.registeredOn = registeredOn;
        this.authorities = authorities;
        this.homesGroups = homesGroups;
        this.cashiers = cashiers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return username;
    }

    public void setEmail(String email) {
        this.username = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<HomesGroup> getHomesGroups() {
        return homesGroups;
    }

    public void setHomesGroups(HomesGroup homesGroup) {
        this.homesGroups.add(homesGroup);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<HomesGroup> getHomesGroup() {
        return homesGroups;
    }

    public List<User> getCashiers() {
        return cashiers;
    }

    public boolean isCashier() {
        return authorities.stream().allMatch(r -> r.getAuthority().equals("ROLE_CASHIER"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public HomesGroup getHomesGroupById(long homesGroupId) {
        return this.homesGroups.stream().filter(hg -> hg.getId() == homesGroupId).findFirst().orElse(null);
    }

    public Home getHomeByIdFromHomesGroup(HomesGroup homesGroup, long homeId) {
        return homesGroup.getHomes().stream().filter(h -> h.getId() == homeId).findFirst().orElse(null);
    }
}
