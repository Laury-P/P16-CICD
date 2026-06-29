package com.openclassroom.eventorias.core.domain.repository

interface AuthRepository {
    fun getUserId() : String?
    suspend fun signOut() : Result<Unit>
}