package com.dariusepure.caractivitylog.data.cars

import com.dariusepure.caractivitylog.domain.VehicleInspection
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.dariusepure.caractivitylog.domain.Car
import com.dariusepure.caractivitylog.domain.MileageLog
import com.dariusepure.caractivitylog.ui.cars.ChatMessage
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

        reference.set(firestoreCar).await()
    }

    fun getCarFlow(carId: String): Flow<Car?> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val car = snapshot?.toObject(FirestoreCar::class.java)?.fromFirebase()
                trySend(car)
            }

        awaitClose { listener.remove() }
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


    fun getInspections(carId: String): Flow<List<VehicleInspection>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("inspections")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots
                    ?.toObjects(FirestoreVehicleInspection::class.java)
                    ?.map { it.fromFirebase() } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addInspection(carId: String, inspection: VehicleInspection) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        
        // 1. Add Inspection
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("inspections")
            .add(inspection.toFirebase())
            .await()

        // 2. Automatically add to mileage history
        addMileageLog(carId, MileageLog(km = inspection.mileage, date = inspection.date))
    }


    suspend fun updateInspection(carId: String, inspection: VehicleInspection) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("inspections")
            .document(inspection.id)
            .set(inspection.toFirebase())
            .await()
    }

    suspend fun deleteInspection(carId: String, inspectionId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("inspections")
            .document(inspectionId)
            .delete()
            .await()
    }

    fun getDiagnosisMessages(carId: String): Flow<List<ChatMessage>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("diagnosis")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots
                    ?.toObjects(FirestoreChatMessage::class.java)
                    ?.map { it.toChatMessage() } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addDiagnosisMessage(carId: String, message: ChatMessage) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("diagnosis")
            .add(FirestoreChatMessage.fromChatMessage(message))
            .await()
    }

    suspend fun clearDiagnosisMessages(carId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val collection = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("diagnosis")
        
        val snapshots = collection.get().await()
        firestore.runBatch { batch ->
            snapshots.documents.forEach { batch.delete(it.reference) }
        }.await()
    }

    fun getFuelLogs(carId: String): Flow<List<com.dariusepure.caractivitylog.domain.FuelLog>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("fuel_logs")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val results = snapshots
                    ?.toObjects(FirestoreFuelLog::class.java)
                    ?.map { it.fromFirebase() } ?: emptyList()

                trySend(results)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addFuelLog(carId: String, log: com.dariusepure.caractivitylog.domain.FuelLog) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        
        // 1. Add Fuel Log
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("fuel_logs")
            .add(log.toFirebase())
            .await()

        // 2. Automatically update general mileage
        addMileageLog(carId, MileageLog(km = log.km, date = log.date))
    }

    suspend fun deleteFuelLog(carId: String, logId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("cars")
            .document(carId)
            .collection("fuel_logs")
            .document(logId)
            .delete()
            .await()
    }
}
