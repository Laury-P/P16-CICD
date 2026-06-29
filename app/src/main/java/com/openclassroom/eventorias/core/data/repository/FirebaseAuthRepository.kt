package com.openclassroom.eventorias.core.data.repository

import android.content.Context
import com.firebase.ui.auth.FirebaseAuthUI
import com.openclassroom.eventorias.core.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authUI: FirebaseAuthUI): AuthRepository {
    override fun getUserId(): String? {
        return authUI.auth.currentUser?.uid
    }

    override suspend fun signOut(): Result<Unit> = runCatching{
        authUI.signOut(context)
    }
}

