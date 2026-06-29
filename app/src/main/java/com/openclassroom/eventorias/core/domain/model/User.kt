package com.openclassroom.eventorias.core.domain.model

data class User(
    val id : String = "",
    val firstname : String = "",
    val lastname : String = "",
    val avatar : String? = null,
    val email : String = "",
)
