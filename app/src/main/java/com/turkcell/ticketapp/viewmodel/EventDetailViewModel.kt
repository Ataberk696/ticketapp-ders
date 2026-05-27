package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class EventDetailUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val error: String? = null,
    val quantities: Map<String, Int> = emptyMap(),
    val totalCents: Long = 0
) {
    val canPurchase: Boolean
        get() = event != null && quantities.any { it.value > 0 }
}



class EventDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailUiState())
    val state : StateFlow<EventDetailUiState> = _state.asStateFlow()

    fun loadEvent(id :String){

        if (_state.value.isLoading) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getEvent(id)
                .onSuccess { event ->
                    _state.update {
                        it.copy(isLoading = false,
                            event = event,
                            quantities = event.ticketTypes.associate { type -> type.id to 0 },
                            totalCents = 0
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false, error = e.message ?: "Etkinlik Yüklenemedi.")
                    }
                }
        }
    }

    fun onIncrease(ticketTypeId: String) {
        _state.update { current ->
            val event = current.event ?: return@update current
            val type = event.ticketTypes.find { it.id == ticketTypeId } ?: return@update current
            val currentQty = current.quantities[ticketTypeId] ?: 0
            val maxQty = minOf(20, type.remaining.toInt())
            if (currentQty >= maxQty) return@update current

            val newQuantities = current.quantities + (ticketTypeId to currentQty + 1)
            val newTotal = calculateTotalCents(event, newQuantities)
            current.copy(quantities = newQuantities, totalCents = newTotal)
        }
    }

    fun onDecrease(ticketTypeId: String) {
        _state.update { current ->
            val currentQty = current.quantities[ticketTypeId] ?: return@update current
            if (currentQty <= 0) return@update current

            val event = current.event ?: return@update current
            val newQuantities = current.quantities + (ticketTypeId to currentQty - 1)
            val newTotal = calculateTotalCents(event, newQuantities)
            current.copy(quantities = newQuantities, totalCents = newTotal)
        }
    }




    private fun calculateTotalCents(event: Event, quantities: Map<String, Int>): Long {
        return event.ticketTypes.sumOf { type ->
            val qty = quantities[type.id] ?: 0
            type.priceCents * qty
        }
    }


}