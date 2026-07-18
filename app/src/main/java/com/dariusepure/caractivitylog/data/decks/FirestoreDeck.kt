package com.dariusepure.caractivitylog.data.decks

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.dariusepure.caractivitylog.domain.Deck

data class FirestoreDeck(
    @DocumentId val id: String,
    val title: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val noCards: Int
)

fun Deck.toFirebase() = FirestoreDeck(
    id = this.id,
    title = this.title,
    createdAt = Timestamp(this.createdAt),
    updatedAt = Timestamp(this.updatedAt),
    noCards = this.noCards
)

fun FirestoreDeck.fromFirebase() = Deck(
    id = this.id,
    title = this.title,
    createdAt = this.createdAt.toDate(),
    updatedAt = this.updatedAt.toDate(),
    noCards = this.noCards
)