package com.simplecoding.devlinkback.global.jwt;

import com.simplecoding.michelin_back.member.entity.Member;
import com.simplecoding.michelin_back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, attributes);

        Member member = saveOrUpdate(oAuth2Attributes);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getMemberGrade())),
                attributes,
                userNameAttributeName
        );
    }

    private Member saveOrUpdate(OAuth2Attributes attributes) {
        // 1순위: provider + providerId 로 조회 (소셜 재로그인 시 정확한 매칭)
        return memberRepository.findByProviderAndProviderId(
                        attributes.getProvider(), attributes.getProviderId())
                .orElseGet(() -> {
                    // 2순위: 동일 이메일로 가입된 일반 회원이 있으면 연동
                    boolean isFallbackEmail = attributes.getEmail().endsWith("@oauth.local");
                    if (!isFallbackEmail) {
                        return memberRepository.findByEmail(attributes.getEmail())
                                .orElseGet(() -> memberRepository.save(attributes.toMember()));
                    }
                    // 3순위: 신규 등록
                    return memberRepository.save(attributes.toMember());
                });
    }
}
