package com.c1b1.v2.studyone.studyone.actions;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import com.c1b1.v2.studyone.studyone.domain.User;
import com.c1b1.v2.studyone.studyone.domain.UserWord;
import com.c1b1.v2.studyone.studyone.repository.DailyWordRepository;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.repository.UserWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component // required so the bean can be found
public class GetDailyWords {
    List<Integer> REMINDER_DAYS = Arrays.asList(new Integer[]{1,4, 9, 16, 30, 50, 75, 99});

    @Autowired
    private DailyWordRepository dailyWords;

    @Autowired
    private UserWordRepository userWords;

    @Autowired
    private UserRepository users;

    public DailyAndRepeatedWords getDailyAndRepeatingWords(User user) {
        DailyAndRepeatedWords.DailyAndRepeatedWordsBuilder dailyAndRepeatedWords = DailyAndRepeatedWords.builder();
        List<UserWord> userWordsByDate = userWords.findTop1ByUserIdOrderByCreateDateDesc(user.getId());

        if (userWordsByDate.isEmpty() || !matchesServerDate(userWordsByDate.get(0).getCreateDate())) {
            // need new static entry from table
            DailyWord toAdd = addNewDailyWord(user, userWordsByDate);
            dailyAndRepeatedWords.wordOfTheDay(toAdd);
        } else {
            // The latest word in the current userWord table is the word of the day.
            dailyAndRepeatedWords.wordOfTheDay(userWordsByDate.get(0).getDailyWord());
        }
        dailyAndRepeatedWords.pastWords(getRepeatedWords(user));
        return dailyAndRepeatedWords.build();
    }

    private List<DailyWord> getRepeatedWords(User user) {
        List<DailyWord> repeated = new ArrayList<>();

        List<UserWord> userWordsByDateDesc = userWords.findTop100ByUserIdOrderByCreateDateDesc(user.getId());

        // We fetch user words by date but we care about the index rather than the date.
        // Note that there is at most one word a day and user words might not be contiguous.
        for (int i = 1; i < userWordsByDateDesc.size(); ++i) {
            if (REMINDER_DAYS.contains(i))
                repeated.add(userWordsByDateDesc.get(i).getDailyWord());
        }

        return repeated;
    }

    private DailyWord addNewDailyWord(User user, List<UserWord> userWordsByDate) {
        DailyWord newDailyWord = userWordsByDate.isEmpty() ?
                dailyWords.findTop1ByIdGreaterThan( /* lowestId= */0).get(0) :
                dailyWords.findTop1ByIdGreaterThan(userWordsByDate.get(0).getDailyWord().getId()).get(0);

        userWords.save(UserWord.builder()
                .user(user)
                .dailyWord(newDailyWord)
                .build());

        return newDailyWord;
    }

    private static boolean matchesServerDate(Date savedDate) {
        Date serverDate = new Date(); // default system date (PST)
        // Default time zone is PST
        LocalDate serverLocalDate = serverDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return toLocalDate(savedDate).equals(serverLocalDate);
    }

    private static  LocalDate toLocalDate(Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime.toLocalDate();
    }
}
