package com.dat_viet_group.datvietgroup.core.security;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.io.IOException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import com.dat_viet_group.datvietgroup.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration 
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Ngăn Spring Boot tự đăng ký JwtAuthFilter vào Tomcat, chỉ chạy trong chuỗi Security
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter filter) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                // 1. Cho qua trang lỗi nội bộ → không còn bị biến 403/404/500 thành 401
                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                .requestMatchers("/error").permitAll()

                // 2. Public
                .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()

                // 3. ADMIN phải đặt TRƯỚC authenticated (giải thích bên dưới)
                .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_PUT_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, Endpoints.ADMIN_PATCH_ENDPOINTS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_DELETE_ENDPOINTS).hasRole("ADMIN")

                // 4. Chỉ cần đăng nhập
                .requestMatchers(HttpMethod.GET, Endpoints.PRIVATE_GET_ENDPOINT).authenticated()
                .requestMatchers(HttpMethod.POST, Endpoints.PRIVATE_POST_ENDPOINT).authenticated()
                .requestMatchers(HttpMethod.PUT, Endpoints.PRIVATE_PUT_ENDPOINT).authenticated()
                .requestMatchers(HttpMethod.DELETE, Endpoints.PRIVATE_DELETE_ENDPOINT).authenticated()
                .anyRequest().authenticated());

        // IF_REQUIRED: OAuth2 cần session tạm thời trong lúc redirect Google
        // Sau khi callback xong thì JWT stateless tiếp quản, session không còn dùng nữa
        // http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Cấu hình OAuth2 Login - chỉ dùng Google để xác thực, sau đó dùng JWT
        // http.oauth2Login(oauth2 -> oauth2
        //         .authorizationEndpoint(auth ->
        //                 auth.baseUri("/api/oauth2/authorization")
        //         )
        //         .successHandler(oAuth2SuccessHandler)
        //         .failureHandler(oAuth2FailureHandler));
            
                // Trả JSON trực tiếp theo format ApiResponse, không đi qua /error
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((req, res, e) ->
                        writeError(res, HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn"))
                .accessDeniedHandler((req, res, e) ->
                        writeError(res, HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này")));
        return http.build();
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String json = String.format("{\"success\":false,\"message\":\"%s\"}", message);
        response.getWriter().write(json);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Endpoints.FRONT_END_HOSTS);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization")); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
}
