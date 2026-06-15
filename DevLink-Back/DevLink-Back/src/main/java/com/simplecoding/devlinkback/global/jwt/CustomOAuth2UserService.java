package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.devlinkback.domain.user.entity.User;
import com.simplecoding.devlinkback.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(provider, attributes);

        User user = saveOrUpdate(oAuth2Attributes);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(
                Collections.singleton(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                user.getRole().name())),
                attributes,
                userNameAttributeName
        );
    }

    private User saveOrUpdate(OAuth2Attributes attributes) {
        return userRepository.findByProviderAndProviderId(
                        attributes.getProvider(), attributes.getProviderId())
                .orElseGet(() -> {
                    // 동일 이메일 일반 회원 있으면 연동
                    boolean isFallbackEmail = attributes.getEmail().endsWith("@kakao.local")
                            || attributes.getEmail().endsWith("@naver.local");
                    if (!isFallbackEmail) {
                        return userRepository.findByEmail(attributes.getEmail())
                                .orElseGet(() -> userRepository.save(
                                        buildUserWithUniqueNickname(attributes)));
                    }
                    return userRepository.save(buildUserWithUniqueNickname(attributes));
                });
    }

    // 닉네임 중복 시 suffix 추가
    private User buildUserWithUniqueNickname(OAuth2Attributes attributes) {
        String nickname = attributes.getNickname();
        String finalNickname = nickname;

        // 닉네임 중복 체크 후 유니크하게 만들기
        int suffix = 1;
        while (userRepository.existsByNickname(finalNickname)) {
            finalNickname = nickname + "_" + suffix;
            suffix++;
        }

        return User.builder()
                .email(attributes.getEmail())
                .nickname(finalNickname)
                .provider(attributes.getProvider())
                .providerId(attributes.getProviderId())
                .role(User.Role.ROLE_USER)
                .build();
    }
}