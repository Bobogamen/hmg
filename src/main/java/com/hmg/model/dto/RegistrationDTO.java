package com.hmg.model.dto;

import com.hmg.model.validation.PasswordMatcher;
import com.hmg.model.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@PasswordMatcher(password = "password",
        confirmPassword = "confirmPassword")
public class RegistrationDTO {

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Email(message = "{valid_email}")
    @UniqueEmail
    private String email;

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 3, max = 30, message = "{field_length}")
    private String name;

    @NotEmpty(message = "{not_empty}")
    @NotBlank(message = "{non-whitespace}")
    @Size(min = 5, max = 20, message = "{password_length}")
    private String password;

    private String confirmPassword;

    public RegistrationDTO() {
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
