package com.turkcell.core.domain

interface EventRepository {
    suspend fun getEvents(upcomingOnly: Boolean = true): Result<List<Event>>
    suspend fun getEventById(id: String): Result<Event>
}