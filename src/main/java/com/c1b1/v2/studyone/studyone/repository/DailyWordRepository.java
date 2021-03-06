package com.c1b1.v2.studyone.studyone.repository;

import com.c1b1.v2.studyone.studyone.domain.DailyWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DailyWordRepository extends JpaRepository<DailyWord, Integer> {

    List<DailyWord> findTop1ByIdGreaterThan(int id);
}