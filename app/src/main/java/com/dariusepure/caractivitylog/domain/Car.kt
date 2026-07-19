package com.dariusepure.caractivitylog.domain

import java.util.Date

data class Car(
    val id: String = "",
    val name: String = "", // Used for License Plate now
    val plateCountry: String = "RO", // Country code (e.g., RO, DE, IT)
    val make: String = "",
    val model: String = "",
    val vin: String = "",
    val year: Int = 0,
    val engineSize: String = "",
    val fuelType: String = "",
    val color: String = "",
    val power: Int = 0,
    val powerUnit: String = "hp",
    val torque: Int = 0,
    val engineCode: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val activityCount: Int = 0
)
