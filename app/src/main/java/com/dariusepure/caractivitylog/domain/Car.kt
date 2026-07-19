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
    val engineLayout: String = "",
    val length: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val wheelbase: Int = 0,
    val trackWidth: Int = 0,
    val emissionStandard: String = "",
    val fuelTankCapacity: Double = 0.0,
    val drivetrain: String = "",
    val gearboxType: String = "",
    val vehicleType: String = "",
    val manufacturingCountry: String = "",
    val profileImageUrl: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val activityCount: Int = 0,
    val isSynced: Boolean = true
)
