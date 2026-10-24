package com.example.myjava3.repository;

import com.example.myjava3.model.BatchStatus;
import com.example.myjava3.model.ProductionBatch;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductionBatchRepo extends BaseRepo<ProductionBatch, Long> {
    List<ProductionBatch> findByStatusIn(List<BatchStatus> statuses);
    long countByRecipeIdAndStatusIn(Long recipeId, List<BatchStatus> statuses);
}