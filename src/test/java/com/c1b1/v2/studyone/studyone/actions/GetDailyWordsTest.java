package com.c1b1.v2.studyone.studyone.actions;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import com.c1b1.v2.studyone.studyone.domain.User;
import com.c1b1.v2.studyone.studyone.domain.UserWord;
import com.c1b1.v2.studyone.studyone.repository.DailyWordRepository;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.repository.UserWordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest // required to satisfy bindings
@ActiveProfiles("test") // required to point to test class
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clears db after each test
public class GetDailyWordsTest {

    @Autowired
    private DailyWordRepository dailyWords;

    @Autowired
    private UserWordRepository userWords;

    @Autowired
    private UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    GetDailyWords getDailyWords;

    private static final DailyWord WORD_0 = DailyWord.builder().word("Apple").meaning("fruit").build();
    private static final DailyWord WORD_1 = DailyWord.builder().word("Car").meaning("zoom zoom").build();
    private static final DailyWord WORD_2 = DailyWord.builder().word("Kitten").meaning("kitty-ta-bo-ni-ta").build();

    private static final List<DailyWord> DAILY_WORDS =
            Arrays.asList(new DailyWord[]{WORD_0, WORD_1, WORD_2});

    private static final String TEST_USERNAME_1 = "testUser1";

    @Test
    public void noUserWords_returnsAddedNewDailyWord() {
        // arrange
        saveUser(TEST_USERNAME_1);
        dailyWords.saveAll(DAILY_WORDS);

        Optional<User> testUser1 = users.findByUsername(TEST_USERNAME_1);
        assertThat(testUser1.get().getUsername())
                .isEqualTo(TEST_USERNAME_1);

        // Act
        DailyWord dailyWord = getDailyWords.getDailyAndRepeatingWords(testUser1.get()).getWordOfTheDay();

        // Assert that word from daily words with lowest id was saved.
        assertThat(dailyWord.getId()).isEqualTo(dailyWords.findTop1ByIdGreaterThan(0).get(0).getId());
    }

    @Test
    public void existingUserWords_returnsLatestFromExisting() {
        // arrange
        saveUser(TEST_USERNAME_1);
        dailyWords.saveAll(DAILY_WORDS);

        User testUser1 = users.findByUsername(TEST_USERNAME_1).get();

        // add existing word
        saveUserWord(testUser1, DAILY_WORDS.get(2)); // saved kitten

        // Act
        // By default timestamp of get call and time stamp of server should match.
        DailyWord dailyWord = getDailyWords.getDailyAndRepeatingWords(testUser1).getWordOfTheDay();

        // Assert that word from daily words with lowest id was saved.
        assertThat(dailyWord.getWord()).isEqualTo(DAILY_WORDS.get(2).getWord());
    }

    @Test
    public void existingUserWords_returnsAddedNewDailyWord() {
        // arrange
        saveUser(TEST_USERNAME_1);
        dailyWords.saveAll(DAILY_WORDS);

        User testUser1 = users.findByUsername(TEST_USERNAME_1).get();
        Date serverDate = new Date(); // default system date (in default timezone)
        Date date2DaysAgo = Date.from(serverDate.toInstant().minusSeconds(86400 * 2));

        DailyWord word1 = dailyWords.findAll().stream().filter(w -> w.getWord().equals(WORD_1.getWord())).findAny().get();

        // let's save word1 with date pointing to 2 days ago
        userWords.save(UserWord.builder().user(testUser1).dailyWord(word1).createDate(date2DaysAgo).build());

        UserWord inserted = userWords.findByUserId(testUser1.getId()).get(0);

        userWords.setCreateDateForUserWord(date2DaysAgo,inserted.getUserWordId());

        DailyWord expectedNewWord = dailyWords.findTop1ByIdGreaterThan(word1.getId()).get(0);

        // Act
        // By default timestamp of get call and time stamp of server should match.
        DailyWord returned = getDailyWords.getDailyAndRepeatingWords(testUser1).getWordOfTheDay();

        // Assert that word from daily words with lowest id was saved.
        assertThat(returned.getId()).isEqualTo(expectedNewWord.getId());
    }

    private void saveUser(String username) {
        User user = User.builder()
                .username(username)
                .password(this.passwordEncoder.encode("password"))
                .roles(Arrays.asList( "ROLE_USER"))
                .build();
        users.save(user);
    }

    private void saveUserWord(User user, DailyWord dailyWord) {
        userWords.save(UserWord.builder().user(user).dailyWord(dailyWord).build());
    }
}

