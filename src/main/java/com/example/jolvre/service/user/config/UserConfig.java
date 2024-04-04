package com.example.jolvre.service.user.config;

import com.example.jolvre.repository.user.UserRepository;
import com.example.jolvre.service.user.UserService;
import com.example.jolvre.service.user.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final UserRepository userRepository;

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository);
    }
}
