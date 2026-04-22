package com.smartqueue.auth_service.service;

import com.smartqueue.auth_service.model.Role;
import com.smartqueue.auth_service.model.User;
import com.smartqueue.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // لو المستخدم موجود — رجّعه، لو لأ — أنشئه
        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .fullName(name)
                    .password("")          // مفيش password في OAuth2
                    .role(Role.USER)
                    .build();
            userRepository.save(newUser);
            log.info("New OAuth2 user created: {}", email);
            return newUser;
        });

        return oAuth2User;
    }
}