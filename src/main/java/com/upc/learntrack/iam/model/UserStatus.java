package com.upc.learntrack.iam.model;

public enum UserStatus {
    PENDING,    // Esperando aprobación y verificación
    ACTIVE,     // Cuenta activa y verificada
    REJECTED    // Rechazada por el admin
}
