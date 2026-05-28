package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.purchase.MyTicket
import com.turkcell.core.domain.purchase.TicketRepository
import com.turkcell.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class MyTicketsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tickets: List<MyTicket> = emptyList(),
    val error: String? = null
)

class MyTicketsViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyTicketsUiState())
    val state : StateFlow<MyTicketsUiState> = _state.asStateFlow()

    init {
        loadTickets()
    }

    fun loadTickets(){
        if (_state.value.isLoading) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _state.update {
                        it.copy(isLoading = false,isRefreshing = false,tickets=tickets,error = null)
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(isLoading = false,isRefreshing = false,error = e.toUserMessage())
                    }
                }
        }

    }

    fun refreshTickets(){
        if (_state.value.isRefreshing) return

        _state.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { tickets ->
                    _state.update { it.copy(isRefreshing = false, tickets = tickets, error = null) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshing = false, error = e.toUserMessage()) }
                }
        }
    }
}