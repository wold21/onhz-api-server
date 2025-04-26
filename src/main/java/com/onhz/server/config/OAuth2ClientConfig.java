package com.onhz.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.function.Consumer;

@Configuration
public class OAuth2ClientConfig {
    @Bean
    public OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizeOAuth2AuthorizationRequest());

        return resolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> customizeOAuth2AuthorizationRequest() {
        return customizer -> {
            String registrationId = customizer.build().getAttribute("registration_id");

            if (registrationId == null) {
                return;
            }

            System.out.println("OAuth2 authorization request for: " + registrationId);

            switch (registrationId.toLowerCase()) {
                case "google":
                    customizer.additionalParameters(params -> {
                        params.put("prompt", "select_account");
                        params.put("access_type", "offline");
                    });
                    break;

                case "naver":
//                    customizer.additionalParameters(params -> {
//                        params.put("auth_type", "reprompt");
//                    });
                    break;

                case "kakao":
                    customizer.additionalParameters(params -> {
                        params.put("prompt", "login");
                    });
                    break;
            }
        };
    }
}
