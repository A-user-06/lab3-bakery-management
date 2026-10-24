package com.example.myjava3.controller;

import com.example.myjava3.model.Ingredient;
import com.example.myjava3.service.IngredientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController extends BaseController<Ingredient, Long, IngredientService> {

    public IngredientController(IngredientService service) {
        super(service);
    }
}