package com.c1b1.v2.studyone.studyone;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import com.c1b1.v2.studyone.studyone.domain.UserWord;
import com.c1b1.v2.studyone.studyone.repository.DailyWordRepository;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.repository.UserWordRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.c1b1.v2.studyone.studyone.domain.User;

import java.util.Arrays;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    UserRepository users;

    @Autowired
    DailyWordRepository dailyWords;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserWordRepository userWords;

    @Override
    public void run(String... args) throws Exception {
        seedUserWord();
        // Uncomment and modify to seed data
//        this.users.save(User.builder()
//                .username("user")
//                .password(this.passwordEncoder.encode("password"))
//                .roles(Arrays.asList( "ROLE_USER"))
//                .build()
//        );
//
//        this.users.save(User.builder()
//                .username("admin")
//                .password(this.passwordEncoder.encode("password"))
//                .roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
//                .build()
//        );
//
//        log.debug("printing all users...");
//        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
    }

    private void seedUserWord() {
        logger.info("Going to add userWord");
        User user = users.findByUsername("shiva").get();
        DailyWord dailyWord = dailyWords.findAll().get(1);

        UserWord userWord = UserWord.builder()
                                .user(user)
                                .dailyWord(dailyWord)
                                .build();

        userWords.save(userWord);
        logger.info("Saved!!");
    }
}
