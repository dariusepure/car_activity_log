package com.dariusepure.caractivitylog.data.cars

import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.MileageLog
import javax.inject.Inject

class CarRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(FirestoreCar::class.java)?.fromFirebase(
                        isSynced = !doc.metadata.hasPendingWrites()
                    )
                } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createCar(car: Car) {
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

        // We don't use withTimeout here anymore. 
        // Firestore will write to local cache immediately (latency compensation).
        // The await() will complete once the local write is successful.
        reference.set(firestoreCar).await()
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

    fun getMileageLogs(carId: String): Flow<List<MileageLog>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("mileage")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots
                    ?.toObjects(FirestoreMileageLog::class.java)
                    ?.map { it.fromFirebase() } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addMileageLog(carId: String, log: MileageLog) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("mileage")
            .add(log.toFirebase())
            .await()
    }

    suspend fun updateMileageLog(carId: String, log: MileageLog) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("mileage")
            .document(log.id)
            .set(log.toFirebase())
            .await()
    }

    suspend fun deleteMileageLog(carId: String, logId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("mileage")
            .document(logId)
            .delete()
            .await()
    }

    suspend fun uploadCarProfileImage(carId: String, imageData: ByteArray): String {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User not logged in")
        val imageRef = storage.reference.child("users/$uid/cars/$carId/profile.jpg")
        
        imageRef.putBytes(imageData).await()
        val downloadUrl = imageRef.downloadUrl.await().toString()
        
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .update("profileImageUrl", downloadUrl)
            .await()
            
        return downloadUrl
    }
}
