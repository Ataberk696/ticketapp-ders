package com.turkcell.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: String,
    val userId: String,
    val status: String,
    val totalCents: Long,
    val createdAt: String,
    val paidAt: String? = null,
    val items: List<PurchaseItemDto>,
    val tickets: List<TicketDto> = emptyList()
)
{
}