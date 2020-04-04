package com.c1b1.v2.studyone.studyone.repository;

import com.c1b1.v2.studyone.studyone.domain.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserWordRepository extends JpaRepository<UserWord, Long> {
    List<UserWord> findByUserId(long userId);

    List<UserWord> findByUserIdOrderByCreateDateDesc(long userId);

    List<UserWord> findTop100ByUserIdOrderByCreateDateDesc(long userId);

    // Consider using findByCreateAfter
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference
}
