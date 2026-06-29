package com.openclassroom.eventorias.core.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    UserRepository {

    override suspend fun getUserById(userId: String): User? {
        val document = firestore.collection("users").document(userId).get().await()
        return document.toObject(User::class.java)
    }

    override suspend fun addUser(user: User): Result<Unit> = runCatching {
        firestore.collection("users").document(user.id).set(user).await()
    }

    override fun observeUserById(userId: String): Flow<User?> {
        return firestore.collection("users")
            .document(userId)
            .snapshots()
            .map { documentSnapshot ->
                documentSnapshot.toObject(User::class.java)
            }
    }

    override suspend fun uploadAvatarPhoto(userId: String, imageUri: Uri): Result<Unit> =
        runCatching {
            val timestamp = System.currentTimeMillis()
            val imageRef = storage.reference.child("images/users/$userId/avatar_$timestamp.jpg")

            imageRef.putFile(imageUri).await()

            val downloadUrl = imageRef.downloadUrl.await().toString()

            firestore.collection("users").document(userId).update("avatar", downloadUrl).await()
        }

    override suspend fun deleteAvatarByUrl(imageUrl: String): Result<Unit> = runCatching {
        val fileReference = storage.getReferenceFromUrl(imageUrl)
        fileReference.delete().await()
    }


}