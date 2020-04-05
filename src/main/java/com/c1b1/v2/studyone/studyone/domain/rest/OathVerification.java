package com.c1b1.v2.studyone.studyone.domain.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OathVerification {
    private String iss;
    private String sub;
    private String azp;
    private String aud;
    private String iat;
    private String exp;
    private String email;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String locale;

    @Override
    public String toString() {
        return "OathVerification{" +
                "iss='" + iss + '\'' +
                "email='" + email + '\'' +
                "aud='" + aud + '\'' +
                "picture='" + picture + '\'' +
                ", sub=" + sub +
                '}';
    }
}
