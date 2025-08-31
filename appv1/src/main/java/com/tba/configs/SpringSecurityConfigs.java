/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tba.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tba.filters.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 *
 * @author Admin
 */
@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "com.tba.controllers",
    "com.tba.repositories",
    "com.tba.services",
    "com.tba.filters",})
public class SpringSecurityConfigs {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(requests -> requests
                // 1. Cho phép các yêu cầu OPTIONS (CORS preflight) đi qua
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 2. CẤU HÌNH TÀI NGUYÊN TĨNH 

                .requestMatchers("/css/**").permitAll() // Cho phép truy cập tất cả các tệp trong thư mục /css/
                .requestMatchers("/js/**").permitAll() // Cho phép truy cập tất cả các tệp trong thư mục /js/
                .requestMatchers("/images/**").permitAll() // Cho phép truy cập tất cả các tệp trong thư mục /images/
                .requestMatchers("/fonts/**").permitAll()
                .requestMatchers("/resources/**").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/login", "/logout", "/access-denied").permitAll()
                .requestMatchers("/api/login", "/api/register", "api/users").permitAll() // Các API login/register
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Các API product công khai

                // 4. Các đường dẫn web admin, yêu cầu xác thực bằng session (form login)
                .requestMatchers("/", "/home").hasAuthority("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 5. Các API bảo mật bằng JWT (JWTFilter sẽ xử lý JWT, sau đó Spring Security kiểm tra role)
                .requestMatchers("/api/secure/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/secure/supplierorder").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/api/secure/supplierorder").hasAnyRole("EMPLOYEE", "SUPPLIER")
                .requestMatchers(HttpMethod.GET, "/api/secure/mysupplierorder/*").hasRole("SUPPLIER")
                .requestMatchers(HttpMethod.GET, "/api/secure/supplierorder/*").hasAnyRole("EMPLOYEE", "SUPPLIER")
                .requestMatchers(HttpMethod.PUT, "/api/secure/supplierorder/*").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.DELETE, "/api/secure/supplierorder/*").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST, "/api/secure/supplierorder/*/import").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.PATCH, "/api/secure/supplierorder/*/status").hasRole("SUPPLIER")
                .requestMatchers(HttpMethod.PATCH, "/api/secure/supplierorder/*/shippingstatus").hasRole("DELIVERY")
                .requestMatchers("/api/secure/companyproduct/*").hasRole("EMPLOYEE")
                .requestMatchers("/api/secure/bills/*").hasRole("EMPLOYEE")
                .requestMatchers("/api/public/vnpay_return").permitAll()
                .requestMatchers("/api/secure/partners/*").hasAnyRole("EMPLOYEE", "SUPPLIER")
                .requestMatchers("/api/secure/suppliers/*").hasRole("EMPLOYEE")
                .requestMatchers("/api/secure/supproducts/*").hasRole("EMPLOYEE")
                .requestMatchers("/api/secure/profile").permitAll()
                .requestMatchers("/api/secure/delivery/*").hasRole("DELIVERY")
                // 6. Mọi request khác còn lại đều phải xác thực (deny by default)
                .anyRequest().authenticated()
                ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true").permitAll()
                ).logout(logout -> logout
                .logoutSuccessUrl("/login").permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                })
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public Cloudinary cloudinary() {
        Cloudinary cloudinary
                = new Cloudinary(ObjectUtils.asMap(
                        "cloud_name", "dcee16rsp",
                        "api_key", "645857166697866",
                        "api_secret", "QpsoRSYSM8S4rzFOS51f3615UmQ",
                        "secure", true));
        return cloudinary;
    }
}
