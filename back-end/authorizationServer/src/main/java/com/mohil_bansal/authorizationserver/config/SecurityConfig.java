package com.mohil_bansal.authorizationserver.config;

import com.mohil_bansal.authorizationserver.service.CustomUserDetailsService;
import com.mohil_bansal.authorizationserver.util.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

//Old Way (Got to know later article was old)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/register", "/auth/login", "/auth/refresh", "/auth/validate", "/auth/logout").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
//@Override
//protected void configure(HttpSecurity http) throws Exception {
//    http.csrf().disable() // Disable CSRF for stateless authentication
//            .authorizeRequests()
//            .antMatchers("/auth/register", "/auth/login", "/auth/refresh", "/auth/validate", "/auth/logout")
//            .permitAll() // Allow unauthenticated access to these endpoints
//            .antMatchers("/auth/ok") // Protect this endpoint and require authentication
//            .authenticated() // Ensure the user is authenticated
//            .anyRequest().authenticated() // Protect all other endpoints
//            .and()
//            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before authentication
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Use stateless authentication
//}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}