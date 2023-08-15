package com.hmg.service.implemetation;

import com.hmg.model.dto.RegistrationDTO;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Role;
import com.hmg.model.entities.User;
import com.hmg.repository.RoleRepository;
import com.hmg.repository.UserRepository;
import com.hmg.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final HttpServletRequest httpServletRequest;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Role admin() {
        return this.roleRepository.getById(1);
    }
    @Override
    public Role manager() {
        return this.roleRepository.getById(2);
    }
    @Override
    public Role cashier() {
        return this.roleRepository.getById(3);
    }
    @Override
    public User getUserById(long id) {
        return this.userRepository.getUserById(id);
    }
    @Override
    public boolean findEmail(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }
    @Override
    public boolean findEmailByToken(String token) {
        return this.userRepository.findByResetPasswordToken(token).isPresent();
    }
    @Override
    public boolean isOwner(long homesGroupId, long userId) {
        return this.userRepository.getUserById(userId).getHomesGroups().stream().anyMatch(hg -> hg.getId() == homesGroupId);
    }

    @Override
    public void register(RegistrationDTO registrationDTO) throws ServletException {
        User newUser = new User();
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setName(registrationDTO.getName());
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        newUser.setRegisteredOn(LocalDate.now());

        if (this.userRepository.count() == 0) {
            newUser.setRoles(admin());
        }

        newUser.setRoles(manager());
        newUser.setRoles(cashier());

        this.userRepository.save(newUser);

        afterRegistrationLogin(httpServletRequest, registrationDTO.getEmail(), registrationDTO.getPassword());
    }

    @Override
    public void afterRegistrationLogin(HttpServletRequest request, String username, String password) throws ServletException {
        request.login(username, password);
    }


    @Override
    public void updateResetPasswordToken(String token, String email) {
        User user = this.userRepository.getUserByEmail(email);
        user.setResetPasswordToken(token);
        this.userRepository.save(user);
    }

    @Override
    public void changePasswordOnEmailByToken(String token, String password) {
        User user = this.userRepository.getUserByResetPasswordToken(token);

        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordToken(null);

        this.userRepository.save(user);
    }

    @Override
    public String getResetPasswordTokenByEmail(String email) {
        return this.userRepository.getResetPasswordTokenByEmail(email);
    }

    @Override
    public void registerCashier(RegistrationDTO registrationDTO, long userId) {
        User cashier = new User();
        cashier.setName(registrationDTO.getName());
        cashier.setEmail(registrationDTO.getEmail());
        cashier.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        cashier.setRegisteredOn(LocalDate.now());
        cashier.setRoles(cashier());

        User manager = getUserById(userId);
        manager.setCashier(cashier);

        this.userRepository.saveAll(List.of(cashier, manager));
    }

    @Override
    public void editUserName(long id, String name) {
        User user = getUserById(id);
        user.setName(name);
        this.userRepository.save(user);
    }

    @Override
    public void changePasswordByUserId(long id, String password) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(password));

        this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public void addHomesGroupToUser(long userId, HomesGroup newHomesGroup) {
        User user = this.userRepository.getUserById(userId);
        user.setHomesGroups(newHomesGroup);

        this.userRepository.save(user);
    }

    @Override
    public void setHomesGroupsToUser(List<HomesGroup> homesGroups, long cashierId) {
        User cashier = getUserById(cashierId);

        if (!homesGroups.isEmpty()) {

            homesGroups.forEach(hg -> {
                boolean isFound = cashier.getHomesGroups().stream().anyMatch(chg -> chg.getId() == hg.getId());

                if (!isFound) {
                    cashier.setHomesGroups(hg);
                } else {
                    cashier.removeHomesGroup(hg);
                }
            });
        } else  {
            cashier.getHomesGroups().clear();
        }

        this.userRepository.save(cashier);
    }
}
