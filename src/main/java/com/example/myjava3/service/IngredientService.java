package com.example.myjava3.service;

import com.example.myjava3.model.Ingredient;
import com.example.myjava3.repository.IngredientRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class IngredientService extends BaseService<Ingredient, Long> {
    private final IngredientRepo ingredientRepo;

    public IngredientService(IngredientRepo ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @Override
    protected JpaRepository<Ingredient, Long> getRepository() {
        return ingredientRepo;
    }
}