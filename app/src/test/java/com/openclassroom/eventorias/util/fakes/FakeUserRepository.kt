package com.openclassroom.eventorias.util.fakes

import android.net.Uri
import com.openclassroom.eventorias.core.domain.model.User
import com.openclassroom.eventorias.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeUserRepository : UserRepository{
    private val users = mutableMapOf<String, User>()

    override suspend fun getUserById(userId: String): User? {
        return users[userId]
    }

    override suspend fun addUser(user: User): Result<Unit> {
        users[user.id] = user
        return Result.success(Unit)
    }

    override fun observeUserById(userId: String): Flow<User?> {
        return flowOf(users[userId])
    }

    override suspend fun uploadAvatarPhoto(
        userId: String,
        imageUri: Uri
    ): Result<Unit> {
        return Result.success(Unit)
    }


    var shouldDeleteAvatarFail = false
    override suspend fun deleteAvatarByUrl(imageUrl: String): Result<Unit> {
        return if (shouldDeleteAvatarFail) {
            Result.failure(RuntimeException("Network error on delete"))
        } else Result.success(Unit)
    }
}