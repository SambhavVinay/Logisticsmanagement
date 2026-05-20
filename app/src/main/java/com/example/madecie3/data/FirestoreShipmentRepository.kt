package com.example.madecie3.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirestoreShipmentRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private fun userShipments(uid: String) = firestore
        .collection("users")
        .document(uid)
        .collection("shipments")

    suspend fun createShipment(uid: String, shipment: FirestoreShipment): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            val data = hashMapOf(
                "trackingId" to shipment.trackingId,
                "sender" to shipment.sender,
                "receiver" to shipment.receiver,
                "pickupAddress" to shipment.pickupAddress,
                "deliveryAddress" to shipment.deliveryAddress,
                "weight" to shipment.weight,
                "cost" to shipment.cost,
                "paymentMethod" to shipment.paymentMethod,
                "status" to shipment.status,
                "createdAt" to FieldValue.serverTimestamp()
            )

            userShipments(uid)
                .add(data)
                .addOnSuccessListener {
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit))
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error))
                    }
                }
        }

    suspend fun findShipmentByTrackingId(
        uid: String,
        trackingId: String
    ): Result<FirestoreShipment?> = suspendCancellableCoroutine { continuation ->
        userShipments(uid)
            .whereEqualTo("trackingId", trackingId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val shipment = snapshot.documents.firstOrNull()?.toObject<FirestoreShipment>()
                if (continuation.isActive) {
                    continuation.resume(Result.success(shipment))
                }
            }
            .addOnFailureListener { error ->
                if (continuation.isActive) {
                    continuation.resume(Result.failure(error))
                }
            }
    }

    suspend fun getShipments(uid: String): Result<List<FirestoreShipment>> =
        suspendCancellableCoroutine { continuation ->
            userShipments(uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val shipments = snapshot.documents.mapNotNull { it.toObject<FirestoreShipment>() }
                    if (continuation.isActive) {
                        continuation.resume(Result.success(shipments))
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error))
                    }
                }
        }

    suspend fun getAllShipments(): Result<List<FirestoreShipment>> =
        suspendCancellableCoroutine { continuation ->
            firestore.collectionGroup("shipments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val shipments = snapshot.documents.mapNotNull { it.toObject<FirestoreShipment>() }
                    if (continuation.isActive) {
                        continuation.resume(Result.success(shipments))
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error))
                    }
                }
        }

    suspend fun updateShipmentStatus(trackingId: String, newStatus: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            firestore.collectionGroup("shipments")
                .whereEqualTo("trackingId", trackingId)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    val doc = snapshot.documents.firstOrNull()
                    if (doc != null) {
                        doc.reference.update("status", newStatus)
                            .addOnSuccessListener {
                                if (continuation.isActive) {
                                    continuation.resume(Result.success(Unit))
                                }
                            }
                            .addOnFailureListener { error ->
                                if (continuation.isActive) {
                                    continuation.resume(Result.failure(error))
                                }
                            }
                    } else {
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(Exception("Shipment not found")))
                        }
                    }
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(error))
                    }
                }
        }
}
