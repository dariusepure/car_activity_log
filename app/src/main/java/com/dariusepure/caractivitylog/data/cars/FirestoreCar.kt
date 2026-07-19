package com.dariusepure.caractivitylog.data.cars

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.dariusepure.caractivitylog.domain.Car

data class FirestoreCar(
    @DocumentId val id: String = "",
    val name: String = "",
    val make: String = "",
    val model: String = "",
    val vin: String = "",
    val year: Int = 0,
    val engineSize: String = "",
    val fuelType: String = "",
    val color: String = "",
    val power: Int = 0,
    val powerUnit: String = "hp",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val activityCount: Int = 0
)

fun Car.toFirebase() = FirestoreCar(
    id = this.id,
    name = this.name,
    make = this.make,
    model = this.model,
    vin = this.vin,
    year = this.year,
    engineSize = this.engineSize,
    fuelType = this.fuelType,
    color = this.color,
    power = this.power,
    powerUnit = this.powerUnit,
    createdAt = Timestamp(this.createdAt),
    updatedAt = Timestamp(this.updatedAt),
    activityCount = this.activityCount
)

fun FirestoreCar.fromFirebase() = Car(
    id = this.id,
    name = this.name,
    make = this.make,
    model = this.model,
    vin = this.vin,
    year = this.year,
    engineSize = this.engineSize,
    fuelType = this.fuelType,
    color = this.color,
    power = this.power,
    powerUnit = this.powerUnit,
    createdAt = this.createdAt.toDate(),
    updatedAt = this.updatedAt.toDate(),
    activityCount = this.activityCount
)
