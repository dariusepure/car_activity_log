package com.dariusepure.caractivitylog.data.cars

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.dariusepure.caractivitylog.domain.Car
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class CarRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val cars: Flow<List<Car>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots
                    ?.toObjects(FirestoreCar::class.java)
                    ?.map { it.fromFirebase() } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createCar(car: Car) {
        // 1. Schimbăm return-ul silențios cu o eroare vizibilă în caz că nu există user logat
        val uid = firebaseAuth.currentUser?.uid
            ?: throw Exception("Eroare: Utilizatorul nu este logat pe Firebase!")

        val firestoreCar = car.toFirebase()

        val reference = if (car.id.isEmpty()) {
            firestore.collection("users")
                .document(uid)
                .collection("cars")
                .document()
        } else {
            firestore.collection("users")
                .document(uid)
                .collection("cars")
                .document(car.id)
        }

        // 2. Înăsprim regulile: Dacă Firebase nu răspunde în 5 secunde, tăiem firul și aruncăm eroare
        withTimeout(5000L) {
            reference.set(firestoreCar).await()
        }
    }

    suspend fun getCar(carId: String): Car? {
        val uid = firebaseAuth.currentUser?.uid ?: return null
        return firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .get()
            .await()
            .toObject(FirestoreCar::class.java)
            ?.fromFirebase()
    }

    suspend fun deleteCar(carId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .delete()
            .await()
    }
}
