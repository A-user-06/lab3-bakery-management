package com.example.myjava3.repository;

import com.example.myjava3.model.Recipe;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepo extends BaseRepo<Recipe, Long> {
}