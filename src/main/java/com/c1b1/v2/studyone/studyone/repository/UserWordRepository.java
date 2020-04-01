package com.c1b1.v2.studyone.studyone.repository;

import com.c1b1.v2.studyone.studyone.domain.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserWordRepository extends JpaRepository<UserWord, Long> {
}