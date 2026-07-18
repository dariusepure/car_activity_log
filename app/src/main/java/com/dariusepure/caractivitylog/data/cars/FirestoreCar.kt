package com.dariusepure.caractivitylog.data.cars

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.dariusepure.caractivitylog.domain.Car

data class FirestoreCar(
    @DocumentId val id: String = "",
    val name: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val activityCount: Int = 0
)

fun Car.toFirebase() = FirestoreCar(
    id = this.id,
    name = this.name,
    createdAt = Timestamp(this.createdAt),
    updatedAt = Timestamp(this.updatedAt),
    activityCount = this.activityCount
)

fun FirestoreCar.fromFirebase() = Car(
    id = this.id,
    name = this.name,
    createdAt = this.createdAt.toDate(),
    updatedAt = this.updatedAt.toDate(),
    activityCount = this.activityCount
)
