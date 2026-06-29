package com.openclassroom.eventorias.core.utils

import com.openclassroom.eventorias.BuildConfig
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.toAPIUrl(zoom : Int = 13, size : String = "340x144", mapType: String = "roadmap") : String {
    val apiKey = BuildConfig.MAPS_API_KEY

    val encodedAddress = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

    return "https://maps.googleapis.com/maps/api/staticmap?center=$encodedAddress&zoom=$zoom&size=$size&maptype=$mapType&markers=color:red%7C$encodedAddress&key=$apiKey"
}