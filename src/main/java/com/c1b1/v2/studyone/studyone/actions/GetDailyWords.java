package com.c1b1.v2.studyone.studyone.actions;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import com.c1b1.v2.studyone.studyone.domain.User;
import com.c1b1.v2.studyone.studyone.domain.UserWord;
import com.c1b1.v2.studyone.studyone.repository.DailyWordRepository;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.repository.UserWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component // required so the bean can be found
public class GetDailyWords {
    @Autowired
    private DailyWordRepository dailyWords;

    @Autowired
    private UserWordRepository userWords;

    @Autowired
    private UserRepository users;

    public DailyWord getWordOfTheDay(User user) {

        List<UserWord> userWordsByDate = userWords.findTop100ByUserIdOrderByCreateDateDesc(user.getId());

        if (userWordsByDate.isEmpty() || !latestUserWordMatchesServerDate(userWordsByDate)) {
            // need new static entry from table
            return addNewDailyWord(user, userWordsByDate);
        }

        // The lastest word in the current userWord table is the word of the day.
        return userWordsByDate.get(0).getDailyWord();
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

    private static boolean latestUserWordMatchesServerDate(List<UserWord> userWordsByDate) {
        UserWord wordWithMaxDate = userWordsByDate.get(0);
        return matchesServerDate(wordWithMaxDate.getCreateDate());
    }

    private static boolean matchesServerDate(Date savedDate) {
        Date serverDate = new Date(); // default system date (PST)

        // Default time zone is PST
        LocalDate serverLocalDate = serverDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Instant savedInstant = Instant.ofEpochMilli(savedDate.getTime());
        LocalDateTime savedLocalDateTime = LocalDateTime.ofInstant(savedInstant, ZoneId.systemDefault());
        LocalDate savedLocalDate = savedLocalDateTime.toLocalDate();
        return savedLocalDate.equals(serverLocalDate);
    }
}
