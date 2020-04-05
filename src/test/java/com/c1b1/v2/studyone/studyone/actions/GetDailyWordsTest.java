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

import java.util.*;
import java.util.stream.Collectors;

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

    private static final long SECONDS_IN_A_DAY = 86400;

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
        DailyAndRepeatedWords dailyAndRepeatedWords = getDailyWords.getDailyAndRepeatingWords(testUser1);

        // Assert that word from daily words with lowest id was saved.
        assertThat(dailyAndRepeatedWords.getWordOfTheDay().getWord()).isEqualTo(DAILY_WORDS.get(2).getWord());

        // Since all user words were saved in the test (at same server date), the repeated list should be
        // empty.
        assertThat(dailyAndRepeatedWords.getPastWords()).isEmpty();
    }

    @Test
    public void existingUserWords_returnsAddedNewDailyWord() {
        // arrange
        saveUser(TEST_USERNAME_1);
        dailyWords.saveAll(DAILY_WORDS);

        User testUser1 = users.findByUsername(TEST_USERNAME_1).get();
        Date serverDate = new Date(); // default system date (in default timezone)
        Date date2DaysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 2));

        DailyWord word1 = dailyWords.findAll().stream().filter(w -> w.getWord().equals(WORD_1.getWord())).findAny().get();

        userWords.save(UserWord.builder().user(testUser1).dailyWord(word1).build());

        UserWord inserted = userWords.findByUserId(testUser1.getId()).get(0);

        // update save date to 2 days ago
        userWords.setCreateDateForUserWord(date2DaysAgo,inserted.getUserWordId());

        DailyWord expectedNewWord = dailyWords.findTop1ByIdGreaterThan(word1.getId()).get(0);

        // Act
        // By default timestamp of get call and time stamp of server should match.
        DailyAndRepeatedWords dailyAndRepeatedWords = getDailyWords.getDailyAndRepeatingWords(testUser1);

        // Assert that word from daily words with lowest id was saved.
        assertThat(dailyAndRepeatedWords.getWordOfTheDay().getId()).isEqualTo(expectedNewWord.getId());

        // There should be exactly 1 part word (the word which was 2 days ago)
        assertThat(dailyAndRepeatedWords.getPastWords().size()).isEqualTo(1);
    }

    @Test
    public void pastWords_contiguous() {
        // arrange
        saveUser(TEST_USERNAME_1);
        User testUser1 = users.findByUsername(TEST_USERNAME_1).get();
        saveDailyWords(51);

        List<DailyWord> savedDailyWords = dailyWords.findAll();
        Date serverDate = new Date(); // default system date (in default timezone)

        List<UserWord> userWordsToSave = savedDailyWords
                .stream()
                .map(dw -> UserWord.builder().user(testUser1).dailyWord(dw).build())
                .collect(Collectors.toList());
        userWords.saveAll(userWordsToSave);

        // 0th indexed word is today
        // 1st indexed word is 1 day ago
        // ith index is i days ago
        List<UserWord> userWordsWithDefaultDates = userWords.findAll();
        Map<Integer, DailyWord> indexToUserWordId = new HashMap<>();

        for (int i = 0; i < userWordsWithDefaultDates.size(); ++i) {
            Date d = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * i));
            userWords.setCreateDateForUserWord(d,userWordsWithDefaultDates.get(i).getUserWordId());
            indexToUserWordId.put(i, userWordsWithDefaultDates.get(i).getDailyWord());
        }

        // Since REMINDER_DAYS = Arrays.asList(new Integer[]{1, 4, 9, 16, 30, 50, 75, 99})
        // we expect ids corresponding to index 1, index 4, index 9, index 16, index 30 and index 50

        // By default timestamp of get call and time stamp of server should match.
        DailyAndRepeatedWords dailyAndRepeatedWords = getDailyWords.getDailyAndRepeatingWords(testUser1);

        assertThat(dailyAndRepeatedWords.getPastWords().size()).isEqualTo(6);
        assertThat(dailyAndRepeatedWords.getPastWords().stream().map(DailyWord::getId))
                .containsExactlyInAnyOrder(indexToUserWordId.get(1).getId(), indexToUserWordId.get(4).getId(), indexToUserWordId.get(9).getId(),
                        indexToUserWordId.get(16).getId(),indexToUserWordId.get(30).getId(),indexToUserWordId.get(50).getId());
    }

    @Test
    public void pastWords_nonContiguous() {
        // arrange
        saveUser(TEST_USERNAME_1);
        User testUser1 = users.findByUsername(TEST_USERNAME_1).get();
        saveDailyWords(6);

        List<DailyWord> savedDailyWords = dailyWords.findAll();
        Date serverDate = new Date(); // default system date (in default timezone)

        List<UserWord> userWordsToSave = savedDailyWords
                .stream()
                .map(dw -> UserWord.builder().user(testUser1).dailyWord(dw).build())
                .collect(Collectors.toList());
        userWords.saveAll(userWordsToSave);

        List<UserWord> userWordsWithDefaultDates = userWords.findAll();
        // 0th element is for day

        // 5th element 1 days ago
        // This is expected in the answer
        Date _1daysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 1));
        userWords.setCreateDateForUserWord(_1daysAgo,userWordsWithDefaultDates.get(5).getUserWordId());

        // 1st element 3 days ago
        Date _3daysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 3));
        userWords.setCreateDateForUserWord(_3daysAgo,userWordsWithDefaultDates.get(1).getUserWordId());

        // 2nd element 4 days ago
        Date _4daysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 4));
        userWords.setCreateDateForUserWord(_4daysAgo,userWordsWithDefaultDates.get(2).getUserWordId());

        // 3rd element 10 days ago
        // This is expected in the answer
        Date _10daysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 10));
        userWords.setCreateDateForUserWord(_10daysAgo,userWordsWithDefaultDates.get(3).getUserWordId());

        // 4rd element 100 days ago
        Date _100daysAgo = Date.from(serverDate.toInstant().minusSeconds(SECONDS_IN_A_DAY * 100));
        userWords.setCreateDateForUserWord(_100daysAgo,userWordsWithDefaultDates.get(4).getUserWordId());

        // By default timestamp of get call and time stamp of server should match.
        DailyAndRepeatedWords dailyAndRepeatedWords = getDailyWords.getDailyAndRepeatingWords(testUser1);

        // we expect
        assertThat(dailyAndRepeatedWords.getPastWords().size()).isEqualTo(2);
        assertThat(dailyAndRepeatedWords.getPastWords().stream().map(DailyWord::getId))
                .containsExactlyInAnyOrder(userWordsWithDefaultDates.get(5).getDailyWord().getId(),
                        userWordsWithDefaultDates.get(3).getDailyWord().getId());
    }

    private void saveDailyWords(int num) {
        List<DailyWord> toSave = new ArrayList<>();
        for (int i = 0; i < num; ++i) {
            String word = "word" + i;
            String meaning = "meaning" + i;
            toSave.add(DailyWord.builder().word(word).meaning(meaning).build());
        }
        dailyWords.saveAll(toSave);
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

