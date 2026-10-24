package com.example.myjava3.model;

public enum RequestStatus {
    PENDING,      // Ожидает одобрения
    APPROVED,     // Одобрена, партия создана
    READY,        // Готова к выдаче
    CANCELLED     // Отменена
}