package com.openclassroom.eventorias.features.events.usecases

import com.openclassroom.eventorias.core.data.repository.FirebaseUserRepository
import com.openclassroom.eventorias.core.domain.repository.EventRepository
import com.openclassroom.eventorias.features.events.eventList.model.ListEventUiModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetUiEventListUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: FirebaseUserRepository
) {
    operator fun invoke(): Flow<Result<List<ListEventUiModel>>> {
        return eventRepository.getListEvent()
            .map { eventList ->
                val uiList = eventList.map { event ->
                    val promoter = userRepository.getUserById(event.promoterId)
                    val promoterURL = promoter?.avatar ?: ""
                    ListEventUiModel(event = event, promoterAvatarUrl = promoterURL)
                }
                Result.success(uiList)
            }
            .catch { exception ->
                emit(Result.failure(exception))
            }
    }

}