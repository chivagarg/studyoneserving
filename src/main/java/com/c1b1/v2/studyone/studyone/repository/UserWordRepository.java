package com.c1b1.v2.studyone.studyone.repository;

import com.c1b1.v2.studyone.studyone.domain.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserWordRepository extends JpaRepository<UserWord, Long> {
    List<UserWord> findByUserId(long userId);

    List<UserWord> findTop1ByUserIdOrderByCreateDateDesc(long userId);

    List<UserWord> findTop100ByUserIdOrderByCreateDateDesc(long userId);

    // Note: For testing only
    @Modifying // seems like hibernate requires entity names here
    @Query("update UserWord uw set uw.createDate = ?1 where uw.id = ?2")
    @Transactional
    int setCreateDateForUserWord(Date createDate, Long id);

    // Consider using findByCreateAfter
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference
}
