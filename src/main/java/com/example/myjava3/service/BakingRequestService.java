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
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BakingRequestService extends BaseService<BakingRequest, Long> {

    @Autowired
    private BakingRequestRepo requestRepo;
    @Autowired
    private ProductionBatchRepo batchRepo;

    @Override
    protected JpaRepository<BakingRequest, Long> getRepository() {
        return requestRepo;
    }

    // === ВОТ ЭТОТ МЕТОД МЫ ДОБАВИЛИ, ЧТОБЫ СТАВИЛОСЬ ВРЕМЯ ===
    @Override
    @Transactional
    public BakingRequest create(BakingRequest request) {
        request.setCreatedAt(LocalDateTime.now());
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
        }
        return super.create(request);
    }
    // ========================================================

    @Transactional
    public BakingRequest approveAndReserve(Long requestId) {
        BakingRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Заявка должна быть в статусе PENDING. Текущий статус: " + request.getStatus());
        }

        Recipe recipe = request.getRecipe();
        int requestedQty = request.getQuantity();

        List<ProductionBatch> activeBatches = batchRepo.findByStatusIn(
                List.of(BatchStatus.PLANNED, BatchStatus.IN_PROGRESS));

        int reservedBatchesQty = activeBatches.stream()
                .filter(b -> b.getRecipe().getId().equals(recipe.getId()))
                .mapToInt(ProductionBatch::getQuantity)
                .sum();

        for (RecipeIngredient ri : recipe.getComposition()) {
            Ingredient ing = ri.getIngredient();
            BigDecimal neededForRequest = ri.getQuantity().multiply(BigDecimal.valueOf(requestedQty));
            BigDecimal alreadyReserved = ri.getQuantity().multiply(BigDecimal.valueOf(reservedBatchesQty));
            BigDecimal available = ing.getStock().subtract(alreadyReserved);

            if (available.compareTo(neededForRequest) < 0) {
                throw new ConflictException(String.format(
                        "Недостаточно ингредиента '%s'. Нужно: %s, свободно: %s",
                        ing.getName(), neededForRequest, available));
            }
        }

        ProductionBatch batch = new ProductionBatch();
        batch.setRecipe(recipe);
        batch.setQuantity(requestedQty);
        batch.setStatus(BatchStatus.PLANNED);
        batch = batchRepo.save(batch);

        request.setBatch(batch);
        request.setStatus(RequestStatus.APPROVED);
        return requestRepo.save(request);
    }

    @Transactional
    public BakingRequest cancelRequest(Long requestId) {
        BakingRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));

        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new ConflictException("Заявка уже отменена");
        }

        if (request.getBatch() != null) {
            BatchStatus batchStatus = request.getBatch().getStatus();
            if (batchStatus == BatchStatus.IN_PROGRESS || batchStatus == BatchStatus.DONE) {
                throw new ConflictException("Невозможно отменить заявку: производство уже " +
                        (batchStatus == BatchStatus.IN_PROGRESS ? "начато" : "завершено"));
            }
            if (batchStatus == BatchStatus.PLANNED) {
                request.getBatch().setStatus(BatchStatus.CANCELLED);
                batchRepo.save(request.getBatch());
            }
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setBatch(null);
        return requestRepo.save(request);
    }
}