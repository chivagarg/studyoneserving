package com.c1b1.v2.studyone.studyone.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayWordForm {
    private String word;
    private String meaning;
    private String pronunciation;
}

