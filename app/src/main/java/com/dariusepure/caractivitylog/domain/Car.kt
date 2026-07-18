package com.dariusepure.caractivitylog.domain

import java.util.Date

data class Car(
    val id: String = "",
    val name: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val activityCount: Int = 0
)
