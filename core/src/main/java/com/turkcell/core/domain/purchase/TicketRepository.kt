package com.turkcell.core.domain.purchase

interface TicketRepository {
    suspend fun getMyTickets(): Result<List<MyTicket>>

    suspend fun getTicketById(ticketId: String): Result<MyTicket>
}