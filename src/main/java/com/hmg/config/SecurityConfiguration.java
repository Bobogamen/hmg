package com.hmg.config;

import com.hmg.model.enums.RoleEnum;
import com.hmg.repository.UserRepository;
import com.hmg.service.implemetation.HomeManagerUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        http.authorizeHttpRequests(request -> {
            request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            request.requestMatchers("/favicon.ico", "/", "/register", "/forgot-password", "/reset-password", "/login-fail").permitAll();
            request.requestMatchers("/admin").hasRole(RoleEnum.ADMIN.name());
            request.requestMatchers("/manager").hasRole(RoleEnum.MANAGER.name());
            request.requestMatchers("/cashier").hasRole(RoleEnum.CASHIER.name());
            request.anyRequest().authenticated();
        });

        http.formLogin(login -> {
            login.
                    loginPage("/").
                    usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY).
                    passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY).
                    defaultSuccessUrl("/profile").
                    failureForwardUrl("/login-fail");
        });

        http.rememberMe(rememberMe -> {
            rememberMe.
                    rememberMeParameter("remember-me").
                    tokenValiditySeconds(7*24*60*60).
                    key("home-manager_remember_me");
        });

        http.logout(logout -> {
            logout.
                    logoutUrl("/logout").
                    logoutSuccessUrl("/").
                    invalidateHttpSession(true).
                    deleteCookies("JSESSIONID");
        });

//        OLD CONFIGURATION PATTERN
//
//        http.
//                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).
//        and().
//                authorizeHttpRequests().
//                requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll().
//                antMatchers("/favicon.ico", "/", "/register", "/forgot-password", "/reset-password").permitAll().
//                antMatchers("/admin").hasRole(RoleEnum.ADMIN.name()).
//                antMatchers("/manager").hasRole(RoleEnum.MANAGER.name()).
//                antMatchers("/cashier").hasRole(RoleEnum.CASHIER.name()).
//                anyRequest().authenticated().
//        and().
//                formLogin().loginPage("/").
//                usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY).
//                passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY).
//                defaultSuccessUrl("/profile").
//                failureForwardUrl("/login-fail").
//        and().
//                rememberMe().
//                rememberMeParameter("remember-me").
//                tokenValiditySeconds(7 * 24 * 60 * 60).
//                key("home-manager_remember_me").
//        and().
//                logout().
//                logoutUrl("/logout").
//                logoutSuccessUrl("/").
//                invalidateHttpSession(true).
//                deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new HomeManagerUserDetailsService(userRepository);
    }
}

