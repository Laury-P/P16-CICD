package com.openclassroom.eventorias.core.utilsTest


import com.openclassroom.eventorias.core.utils.toFormattedDateString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime

class DateFormatterTest {

    companion object{
        @JvmStatic
        fun localDateTimeProvider() : List<Arguments> {
            return listOf(
                Arguments.of(LocalDateTime.of(2026,6,15,20,0), "June 15, 2026"),
                Arguments.of(LocalDateTime.of(2026,7,20,12,30), "July 20, 2026"),
                Arguments.of(LocalDateTime.of(2026,11,4,2,10), "November 4, 2026"),
            )
        }

        @JvmStatic
        fun localDateProvider() : List<Arguments> {
            return listOf(
                Arguments.of(LocalDate.of(2026,6,15), "June 15, 2026"),
                Arguments.of(LocalDate.of(2026,7,20), "July 20, 2026"),
                Arguments.of(LocalDate.of(2026,11,4), "November 4, 2026"),
            )
        }

    }

    @ParameterizedTest(name = "Date {0} should be formatted as {1}")
    @MethodSource("localDateTimeProvider")
    fun `given a local date time, when formatting, then returns english formatted string`(date: LocalDateTime, expected:String) {

        // WHEN
        val result = date.toFormattedDateString()

        // THEN
        assertEquals(expected, result)
    }

    @ParameterizedTest(name = "Date {0} should be formatted as {1}")
    @MethodSource("localDateProvider")
    fun `given a local date, when formatting, then returns english formatted string`(date: LocalDate, expected:String) {

        // WHEN
        val result = date.toFormattedDateString()

        // THEN
        assertEquals(expected, result)
    }
}