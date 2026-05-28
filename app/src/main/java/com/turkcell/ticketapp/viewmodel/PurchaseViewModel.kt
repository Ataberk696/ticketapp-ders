package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.core.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PurchaseUiState(
    val isCreating: Boolean = false,
    val isPaying: Boolean = false,
    val purchase: Purchase? = null,
    val error: String? = null,
    val showConfirmation: Boolean = false,
    val isPaid: Boolean = false
)


class PurchaseViewModel(
    private val purchaseRepository: PurchaseRepository
): ViewModel() {

    private val _state = MutableStateFlow(PurchaseUiState())
    val state: StateFlow<PurchaseUiState> = _state.asStateFlow()

    fun createPurchase(items: List<Pair<String, Int>>) {

        if (_state.value.isCreating) return

        _state.update { it.copy(isCreating = true, error = null) }
        viewModelScope.launch {
            purchaseRepository.createPurchase(items)
                .onSuccess { purchase ->
                    _state.update { it.copy(isCreating = false, purchase = purchase, showConfirmation = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isCreating = false, error = e.toUserMessage()) }
                }
        }

    }

    fun confirmPayment(){
        val purchaseId = _state.value.purchase?.id ?: return
        if (_state.value.isPaying) return

        _state.update { it.copy(isPaying = true, showConfirmation = false, error = null) }
        viewModelScope.launch {
            purchaseRepository.payPurchase(purchaseId)
                .onSuccess { purchase ->
                    _state.update { it.copy(isPaying = false, purchase = purchase, isPaid = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isPaying = false, error = e.toUserMessage()) }
                }
        }
    }


    fun dismissConfirmation(){
        _state.update { it.copy(showConfirmation = false) }
    }

    fun consumeError(){
        _state.update { it.copy(error = null) }
    }

}