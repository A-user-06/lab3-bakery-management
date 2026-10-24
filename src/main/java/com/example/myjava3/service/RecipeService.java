package com.example.myjava3.service;

import com.example.myjava3.model.Recipe;
import com.example.myjava3.repository.RecipeRepo;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;

@Service
public class RecipeService extends BaseService<Recipe, Long> {

    private final RecipeRepo recipeRepo;

    public RecipeService(RecipeRepo recipeRepo) {
        this.recipeRepo = recipeRepo;
    }

    @Override
    protected JpaRepository<Recipe, Long> getRepository() {
        return recipeRepo;
    }
}