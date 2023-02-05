package com.config;

import com.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
public class SecurityConfig {

    private final SecurityService securityService;
    private final AuthSuccessHandler authSuccessHandler;
    public SecurityConfig(SecurityService securityService, AuthSuccessHandler authSuccessHandler) {
        this.securityService = securityService;
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests() // everything in this page is authorized
                .antMatchers("/company/**").hasAuthority("Root User")
                .antMatchers("/user/**").hasAnyAuthority("Root User", "Admin")
                .antMatchers("/clientVendor/**").hasAnyAuthority("Admin", "Manager", "Employee")
                .antMatchers("/category/**").hasAnyAuthority("Admin","Manager", "Employee")
                .antMatchers("/product/**").hasAnyAuthority("Admin","Manager", "Employee")
                .antMatchers("/report/**").hasAnyAuthority("Admin", "Manager")
                .antMatchers("/dashboard").hasAnyAuthority("Manager", "Employee")
                .antMatchers("/invoice/**").hasAnyAuthority("Manager", "Employee", "Admin" )
                .antMatchers(               // except these
                        "/",
                        "/login",
                        "/fragments/**",    // /-> means everything under fragments
                        "/assets/**",
                        "/images/**"
                ).permitAll() // available for everyone, anybody can access
                .anyRequest().authenticated() // any other request authenticated
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(authSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .and()
                .rememberMe()
                .tokenValiditySeconds(86400)
                .key("djrProject")
                .userDetailsService(securityService)
                .and()
                .build();
    }
}
