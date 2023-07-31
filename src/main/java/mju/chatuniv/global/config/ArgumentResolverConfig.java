package mju.chatuniv.global.config;

import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.auth.support.JwtLoginResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {

    private final JwtAuthService jwtAuthService;

    public ArgumentResolverConfig(final JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginMemberArgumentResolver());
    }

    @Bean
    public JwtLoginResolver loginMemberArgumentResolver() {
        return new JwtLoginResolver(jwtAuthService);
    }
}
