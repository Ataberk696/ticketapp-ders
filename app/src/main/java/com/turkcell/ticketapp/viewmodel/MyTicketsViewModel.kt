package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTicketsUiState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MyTicketsViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MyTicketsUiState())
    val state: StateFlow<MyTicketsUiState> = _state.asStateFlow()

    init {
        loadTickets()
    }

    private fun loadTickets() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _state.update { it.copy(isLoading = false, tickets = tickets) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}