package com.c1b1.v2.studyone.studyone.auth;

import com.c1b1.v2.studyone.studyone.domain.rest.OathVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OathVerifier {
    private static final String CLIENT_ID = "349372081929-amvj2cn1vkk4gp3u6dk2c23q0mrms6l7.apps.googleusercontent.com";

    @Autowired
    RestTemplate restTemplate;

    OathVerification verify(String oathToken) {
        String getRequest = "https://oauth2.googleapis.com/tokeninfo?id_token="
                + oathToken;
            OathVerification verification = restTemplate.getForObject(
                    getRequest, OathVerification.class);
            System.out.println(verification.toString());
            // Additionally need to make sure that this token is meant for
            // studyONE
            // https://developers.google.com/identity/sign-in/android/backend-auth
            if (!verification.getAud().equals(CLIENT_ID))
                throw new IllegalArgumentException("Aud does not match client id");
            return verification;
    }
}
