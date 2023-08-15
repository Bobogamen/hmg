package com.hmg.service;

import com.hmg.model.dto.RegistrationDTO;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Role;
import com.hmg.model.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    Role admin();

    Role manager();

    Role cashier();

    void register(RegistrationDTO registrationDTO) throws ServletException;

    void afterRegistrationLogin(HttpServletRequest request, String username, String password) throws ServletException;

    boolean findEmail(String email);

    void updateResetPasswordToken(String token, String email);

    boolean findEmailByToken(String token);

    void changePasswordOnEmailByToken(String token, String password);

    String getResetPasswordTokenByEmail(String email);

    User getUserById(long id);

    boolean isOwner(long homesGroupId, long userId);

    void registerCashier(RegistrationDTO registrationDTO, long id);

    void editUserName(long id, String name);

    void changePasswordByUserId(long id, String password);

    List<User> getAllUsers();

    void addHomesGroupToUser(long userId, HomesGroup newHomesGroup);

    void setHomesGroupsToUser(List<HomesGroup> homesGroups, long cashierId);

}
