package com.c1b1.v2.studyone.studyone;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import com.c1b1.v2.studyone.studyone.domain.DisplayWordForm;
import com.c1b1.v2.studyone.studyone.domain.User;
import com.c1b1.v2.studyone.studyone.domain.UserWord;
import com.c1b1.v2.studyone.studyone.repository.DailyWordRepository;
import com.c1b1.v2.studyone.studyone.repository.UserRepository;
import com.c1b1.v2.studyone.studyone.repository.UserWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@Controller // This means that this class is a Controller
@RequestMapping(path="/v1/words") // This means URL's start with /demo (after Application path)
public class MainController {

    Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired // This means to get the bean called dailyWordRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private DailyWordRepository dailyWords;

    @Autowired
    private UserWordRepository userWords;

    @Autowired
    private UserRepository users;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity add (HttpServletRequest request, @RequestParam String word
            , @RequestParam String meaning) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        DailyWord w = DailyWord.builder()
                .word(word)
                .meaning(meaning)
                .build();
        dailyWords.save(w);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromContextPath(request)
                        .path("/v1/words/{id}")
                        .buildAndExpand(w.getId())
                        .toUri())
                .build();
    }

    @PostMapping(path="/create") // Map ONLY POST Requests
    public @ResponseBody ResponseEntity create(HttpServletRequest request, @RequestBody DisplayWordForm form) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        DailyWord w = DailyWord.builder()
                .word(form.getWord())
                .meaning(form.getMeaning())
                .build();
        dailyWords.save(w);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromContextPath(request)
                        .path("/v1/words/{id}")
                        .buildAndExpand(w.getId())
                        .toUri())
                .build();
    }


    @GetMapping(path="/dailywords")
    public @ResponseBody
    ResponseEntity dailyWords(@AuthenticationPrincipal UserDetails userDetails){

        Optional<User> user =  users.findByUsername(userDetails.getUsername());

        if (!user.isPresent())
            throw new InsufficientAuthenticationException("User is not authorized");

        List<UserWord> userWordList = userWords.findByUserId(user.get().getId());

        // This returns a JSON or XML with the words
        return ok(userWordList);
    }

    @GetMapping(path="/all")
    public @ResponseBody
    ResponseEntity getAllWords() {
        // This returns a JSON or XML with the words
        return ok(dailyWords.findAll());
    }
}