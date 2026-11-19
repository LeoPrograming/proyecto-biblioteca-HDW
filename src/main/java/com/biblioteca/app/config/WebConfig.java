package com.biblioteca.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    public WebConfig(
            RoleInterceptor roleInterceptor) {

        this.roleInterceptor =
                roleInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry) {

        // Todas las rutas pasan por el interceptor
        // (incluidas las que se agreguen en el futuro),
        // excepto las públicas y los recursos estáticos.
        registry
                .addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/error",
                        "/api/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/images/**",
                        "/favicon.ico"
                );
    }
}