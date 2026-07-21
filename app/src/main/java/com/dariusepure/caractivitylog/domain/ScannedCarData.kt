package com.dariusepure.caractivitylog.domain

import kotlinx.serialization.Serializable

@Serializable
data class ScannedCarData(
    val make: String? = null,
    val model: String? = null,
    val vin: String? = null,
    val year: Int? = null,
    val fuelType: String? = null,
    val engineSize: Int? = null,
    val power: Int? = null,
    val powerUnit: String? = "hp",
    val torque: Int? = null,
    val color: String? = null,
    val registrationPlate: String? = null,
    val numberOfSeats: Int? = null,
    val numberOfDoors: Int? = null,
    val weight: Int? = null,
    val engineCode: String? = null,
    val emissionStandard: String? = null,
    val gearboxType: String? = null,
    val drivetrain: String? = null,
    val fuelTankCapacity: Double? = null,
    val topSpeed: Double? = null
)
