package com.turkcell.core.domain.purchase

data class MyTicket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus,
    val ticketTypeName: String,
    val eventName: String,
    val eventVenue: String,
    val eventStartsAt: String
) {
}