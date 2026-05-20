package com.turkcell.data.remote

import com.turkcell.data.dto.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TicketApi {
    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getTicketById(@Path("id") id: String): TicketDto
}