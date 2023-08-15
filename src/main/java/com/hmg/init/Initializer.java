package com.hmg.init;

import com.hmg.model.entities.Role;
import com.hmg.model.enums.RoleEnum;
import com.hmg.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class Initializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public Initializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (this.roleRepository.count() == 0) {
            List<Role> roles = Arrays.stream(RoleEnum.values()).map(Role::new).toList();

            this.roleRepository.saveAll(roles);
        }
    }
}
