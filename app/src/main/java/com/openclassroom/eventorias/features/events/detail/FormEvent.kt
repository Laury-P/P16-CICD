package com.openclassroom.eventorias.features.events.detail

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.core.domain.model.EventCategory
import java.time.LocalDate
import java.time.LocalTime


sealed interface FormEvent {
    data class TitleChanged(val title : String) : FormEvent

    data class DescriptionChanged(val description : String) : FormEvent

    data class DateChanged(val date : LocalDate) : FormEvent

    data class TimeChanged(val time : LocalTime) : FormEvent

    data class AddressChanged (val address : String) : FormEvent

    data class PhotoSelected(val uri : Uri) : FormEvent

    data class CategoryChanged(val category: EventCategory) : FormEvent

    data class OnSaveClicked(val context: Context) : FormEvent
}

sealed interface IsPublishing{
    object Idle : IsPublishing
    object Publishing : IsPublishing
    object Published : IsPublishing
    data class Error(val error: PublishError) : IsPublishing
}

sealed class PublishError(@StringRes val messageRes: Int) {
    data object NetworkError : PublishError(R.string.network_error)
    data object UserNotLoggedIn : PublishError(R.string.logged_out_error)
    data object UnknownError : PublishError(R.string.unknown_error)

}