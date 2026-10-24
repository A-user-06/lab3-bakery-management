package com.example.myjava3.controller;

import com.example.myjava3.model.BakingRequest;
import com.example.myjava3.service.BakingRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baking-requests")
public class BakingRequestController extends BaseController<BakingRequest, Long, BakingRequestService> {

    private final BakingRequestService requestService;

    public BakingRequestController(BakingRequestService service) {
        super(service);
        this.requestService = service;
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BakingRequest> approve(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.approveAndReserve(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BakingRequest> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.cancelRequest(id));
    }
}