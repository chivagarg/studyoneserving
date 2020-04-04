package com.c1b1.v2.studyone.studyone.actions;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAndRepeatedWords {
    private DailyWord wordOfTheDay;
    private List<DailyWord> pastWords;
}
