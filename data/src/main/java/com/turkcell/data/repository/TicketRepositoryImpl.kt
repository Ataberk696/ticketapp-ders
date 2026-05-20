package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.Ticket
import com.turkcell.core.domain.TicketRepository
import com.turkcell.core.domain.TicketType
import com.turkcell.data.dto.TicketDto
import com.turkcell.data.remote.TicketApi
import com.turkcell.data.util.runCatchingApi
// Not :  bu kısmı ai'dan yaptım


class TicketRepositoryImpl(private val ticketapi: TicketApi) : TicketRepository {

    override suspend fun getMyTickets(): Result<List<Ticket>> =
        runCatchingApi {
            ticketapi.getMyTickets()
        }.map { list -> list.map { it.toDomain() } }

    override suspend fun getTicketById(id: String): Result<Ticket> =
        runCatchingApi {
            ticketapi.getTicketById(id)
        }.map { it.toDomain() }

    private fun TicketDto.toDomain(): Ticket {
        // ticketType içindeki event DTO'su varsa onu dönüştür
        val event = ticketType.event?.let { eventDto ->
            Event(
                id = eventDto.id,
                name = eventDto.name,
                description = eventDto.description,
                venue = eventDto.venue,
                startsAt = eventDto.startsAt,
                endsAt = eventDto.endsAt,
                createdAt = eventDto.createdAt,
                ticketTypes = emptyList() // bilet detayında etkinliğin tüm türlerine gerek yok
            )
        } ?: Event("", "", "", "", "", "", "", emptyList()) // fallback (olmamalı)

        return Ticket(
            id = id,
            qrCode = qrCode,
            status = status,
            usedAt = usedAt,
            checkedInBy = checkedInBy,
            ticketType = TicketType(
                id = ticketType.id,
                name = ticketType.name,
                priceCents = ticketType.priceCents,
                capacity = 0,
                soldCount = 0,
                remaining = 0
            ),
            event = event
        )
    }
}