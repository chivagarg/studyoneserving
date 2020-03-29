package com.c1b1.v2.studyone.studyone;

import org.springframework.data.repository.CrudRepository;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DailyWordRepository extends CrudRepository<DailyWord, Integer> {
}