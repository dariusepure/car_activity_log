package com.dariusepure.caractivitylog.domain

import kotlinx.serialization.Serializable

@Serializable
data class ScannedMileageEntry(
    val km: Double,
    val date: String? = null // Format: YYYY-MM-DD
)

@Serializable
data class ScannedCarData(
    val make: String? = null,
    val model: String? = null,
    val vin: String? = null,
    val year: Double? = null,
    val fuelType: String? = null,
    val engineSize: Double? = null,
    val power: Double? = null,
    val powerUnit: String? = "hp",
    val torque: Double? = null,
    val color: String? = null,
    val registrationPlate: String? = null,
    val numberOfSeats: Double? = null,
    val numberOfDoors: Double? = null,
    val weight: Double? = null,
    val engineCode: String? = null,
    val emissionStandard: String? = null,
    val gearboxType: String? = null,
    val gears: String? = null,
    val drivetrain: String? = null,
    val engineLayout: String? = null,
    val cylinderLayout: String? = null,
    val fuelTankCapacity: Double? = null,
    val topSpeed: Double? = null,
    val mileage: Double? = null,
    val mileageHistory: List<ScannedMileageEntry>? = null
)
