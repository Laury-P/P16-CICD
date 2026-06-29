package com.openclassroom.eventorias.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val displayDateFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy").withLocale(Locale.ENGLISH)
val displayTimeFormatter : DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
fun LocalDateTime.toFormattedDateString(): String {
    return this.format(displayDateFormatter)
}

fun LocalDate.toFormattedDateString(): String {
    return this.format(displayDateFormatter)
}

fun LocalDateTime.toFormattedTimeString(): String {
    return this.format(displayTimeFormatter)
}

val addFormDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
val addFormTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun LocalDate.toAddFormFormat() : String {
    return this.format(addFormDateFormatter)
}

fun LocalTime.toAddFormFormat() : String {
    return this.format((addFormTimeFormatter))
}
