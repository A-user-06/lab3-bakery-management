package com.example.myjava3.service;

import com.example.myjava3.exception.ConflictException;
import com.example.myjava3.exception.NotFoundException;
import com.example.myjava3.model.*;
import com.example.myjava3.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductionBatchService extends BaseService<ProductionBatch, Long> {

    @Autowired
    private ProductionBatchRepo batchRepo;
    @Autowired
    private IngredientRepo ingredientRepo;
    @Autowired
    private BakingRequestRepo requestRepo;

    @Override
    protected JpaRepository<ProductionBatch, Long> getRepository() {
        return batchRepo;
    }

    @Transactional
    public ProductionBatch startProduction(Long batchId) {
        ProductionBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new NotFoundException("Партия не найдена: " + batchId));

        if (batch.getStatus() != BatchStatus.PLANNED) {
            throw new ConflictException("Партия должна быть в статусе PLANNED");
        }

        for (RecipeIngredient ri : batch.getRecipe().getComposition()) {
            Ingredient ing = ri.getIngredient();
            BigDecimal toDeduct = ri.getQuantity().multiply(BigDecimal.valueOf(batch.getQuantity()));

            if (ing.getStock().compareTo(toDeduct) < 0) {
                throw new ConflictException("Ингредиент '" + ing.getName() + "' исчерпан на складе.");
            }
            ing.setStock(ing.getStock().subtract(toDeduct));
            ingredientRepo.save(ing);
        }

        batch.setStatus(BatchStatus.IN_PROGRESS);
        return batchRepo.save(batch);
    }

    @Transactional
    public ProductionBatch completeProduction(Long batchId) {
        ProductionBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new NotFoundException("Партия не найдена: " + batchId));

        if (batch.getStatus() != BatchStatus.IN_PROGRESS) {
            throw new ConflictException("Партия должна быть в статусе IN_PROGRESS");
        }

        batch.setStatus(BatchStatus.DONE);
        batchRepo.save(batch);

        List<BakingRequest> linkedRequests = requestRepo.findByBatchId(batchId);
        for (BakingRequest req : linkedRequests) {
            if (req.getStatus() == RequestStatus.APPROVED) {
                req.setStatus(RequestStatus.READY);
                requestRepo.save(req);
            }
        }
        return batch;
    }
}