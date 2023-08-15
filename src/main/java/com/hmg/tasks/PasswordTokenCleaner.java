package com.hmg.tasks;

import com.hmg.model.entities.User;
import com.hmg.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswordTokenCleaner {

    private final UserRepository userRepository;

    public PasswordTokenCleaner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "@midnight", zone = "Europe/Sofia")
    public void deleteAllPasswordResetTokens() {
        List<User> allUsersWithPasswordResetToken = this.userRepository.findAllByResetPasswordTokenNotNull();

        if (!allUsersWithPasswordResetToken.isEmpty()) {
            allUsersWithPasswordResetToken.forEach(u -> u.setResetPasswordToken(null));
            this.userRepository.saveAll(allUsersWithPasswordResetToken);
        }
    }
}
