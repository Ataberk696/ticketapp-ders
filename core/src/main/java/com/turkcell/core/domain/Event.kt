package com.turkcell.core.domain

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val venue: String,
    val startsAt: String,
    val endsAt: String,
    val createdAt: String,
    val ticketTypes: List<TicketType>
)
{

}