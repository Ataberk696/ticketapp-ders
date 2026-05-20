package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val name: String,
    val description: String,
    val venue: String,
    val startsAt: String,
    val endsAt: String,
    val createdAt: String,
    val ticketTypes: List<TicketTypeDto>
) {
}