package com.turkcell.core.domain

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<Ticket>>
    suspend fun getTicketById(id: String): Result<Ticket>
}