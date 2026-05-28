package com.turkcell.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class MyTicketDto(
    val id: String,
    val qrCode: String,
    val status: String,
    val usedAt: String? = null,
    val checkedInBy: String? = null,
    val ticketType: TicketTypeDetailDto
) {
}

@Serializable
data class TicketTypeDetailDto(
    val id: String,
    val name: String,
    val priceCents: Long,
    val event: EventSummaryDto
)

@Serializable
data class EventSummaryDto(
    val id: String,
    val name: String,
    val place: String,
    val startsAt: String
)