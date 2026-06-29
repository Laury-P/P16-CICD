package com.openclassroom.eventorias.util.fakes

import com.openclassroom.eventorias.core.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var currentUserId : String? = null

    override fun getUserId(): String? = currentUserId

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }
}