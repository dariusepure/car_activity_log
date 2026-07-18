package com.dariusepure.caractivitylog.domain

import java.util.Date

data class Deck(
    val id: String = "",
    val title: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val noCards: Int = 0
)
