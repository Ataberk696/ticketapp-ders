package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketTypeDto(
    val id: String,
    val name: String,
    val priceCents: Int,
    val capacity: Int = 0,   // bilet detayında kapasite gelmezse default değeri olsun.
    val soldCount: Int = 0,
    val remaining: Int = 0,
    val event: EventDto? = null
)