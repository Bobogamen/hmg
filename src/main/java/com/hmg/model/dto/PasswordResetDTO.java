package com.hmg.model.dto;

import com.hmg.model.validation.PasswordMatcher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@PasswordMatcher(password = "password",
        confirmPassword = "confirmPassword")
public class PasswordResetDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 5, max = 20, message = "{password_length}")
    private String password;

    private String confirmPassword;

    public PasswordResetDTO() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
