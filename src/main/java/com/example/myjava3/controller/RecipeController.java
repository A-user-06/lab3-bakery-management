package com.example.myjava3.controller;

import com.example.myjava3.model.Recipe;
import com.example.myjava3.service.RecipeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController extends BaseController<Recipe, Long, RecipeService> {

    public RecipeController(RecipeService service) {
        super(service);
    }
}