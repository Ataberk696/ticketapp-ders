package com.turkcell.core.domain

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: String,
    val usedAt: String?,
    val checkedInBy: String?,
    val ticketType: TicketType,
    val event: Event
)
{
}