package com.example.myjava3.repository;

import com.example.myjava3.model.BakingRequest;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BakingRequestRepo extends BaseRepo<BakingRequest, Long> {

    // Найти все заявки, привязанные к конкретной партии (нужно для операции 3)
    List<BakingRequest> findByBatchId(Long batchId);
}