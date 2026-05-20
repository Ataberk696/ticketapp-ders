package com.turkcell.data.repository

import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.TicketType
import com.turkcell.data.dto.EventDto
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi

// Not :  bu kısmı ai'dan yaptım

class EventRepositoryImpl(private val eventApi: EventApi): EventRepository {

    override suspend fun getEvents(upcomingOnly: Boolean): Result<List<Event>> =
        runCatchingApi {
            eventApi.getEvents(if (upcomingOnly) true else null)
        }.map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getEventById(id: String): Result<Event> =
        runCatchingApi {
            eventApi.getEventById(id)
        }.map { it.toDomain() }

    private fun EventDto.toDomain() = Event(
        id = id,
        name = name,
        description = description,
        venue = venue,
        startsAt = startsAt,
        endsAt = endsAt,
        createdAt = createdAt,
        ticketTypes = ticketTypes.map { typeDto ->
            TicketType(
                id = typeDto.id,
                name = typeDto.name,
                priceCents = typeDto.priceCents,
                capacity = typeDto.capacity,
                soldCount = typeDto.soldCount,
                remaining = typeDto.remaining
            )
        }
    )

}