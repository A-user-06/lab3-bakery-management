package com.example.myjava3.controller;

import com.example.myjava3.model.ProductionBatch;
import com.example.myjava3.service.ProductionBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-batches")
public class ProductionBatchController extends BaseController<ProductionBatch, Long, ProductionBatchService> {

    private final ProductionBatchService batchService;

    public ProductionBatchController(ProductionBatchService service) {
        super(service);
        this.batchService = service;
    }

    // ОПЕРАЦИЯ 2: Запуск производства (доступно только BAKER и ADMIN)
    @PreAuthorize("hasAnyRole('BAKER', 'ADMIN')")
    @PostMapping("/{id}/start")
    public ResponseEntity<ProductionBatch> startProduction(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.startProduction(id));
    }

    // ОПЕРАЦИЯ 3: Завершение производства (доступно только BAKER и ADMIN)
    @PreAuthorize("hasAnyRole('BAKER', 'ADMIN')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<ProductionBatch> completeProduction(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.completeProduction(id));
    }
}