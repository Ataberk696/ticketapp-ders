package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.checkin.CheckinRepository
import com.turkcell.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffUiState(
    val isLoading: Boolean = false,
    val scanResultMessage: String? = null,
    val errorMessage: String? = null
)

class StaffViewModel(
    private val checkinRepository: CheckinRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StaffUiState())
    val state: StateFlow<StaffUiState> = _state.asStateFlow()

    fun scanQrCode(qrCode: String) {
        if (_state.value.isLoading) return

        _state.update { it.copy(isLoading = true, scanResultMessage = null, errorMessage = null) }

        viewModelScope.launch {
            checkinRepository.scanQr(qrCode)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            scanResultMessage = "Check-in başarılı: ${result.ticketType} - ${result.eventName}"
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage()
                        )
                    }
                }
        }
    }

    fun consumeMessages() {
        _state.update { it.copy(scanResultMessage = null, errorMessage = null) }
    }

    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}