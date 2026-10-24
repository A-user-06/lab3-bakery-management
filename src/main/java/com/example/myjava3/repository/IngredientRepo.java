package com.example.myjava3.repository;

import com.example.myjava3.model.Ingredient;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepo extends BaseRepo<Ingredient, Long> {
    // Специфичные запросы для ингредиентов пока не нужны,
    // базовых CRUD-методов достаточно.
}