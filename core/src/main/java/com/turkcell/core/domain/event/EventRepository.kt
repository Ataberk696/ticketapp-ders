package com.turkcell.core.domain.event

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
    suspend fun getEvent(id: String): Result<Event>
}