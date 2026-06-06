package com.turkcell.data.dto.checkin

import kotlinx.serialization.Serializable

@Serializable
data class ScanResponseDto(
    val ticketId: String,
    val ticketType: String,
    val event: ScanEventDto,
    val checkedInAt: String
)

@Serializable
data class ScanEventDto(
    val id: String,
    val name: String,
    val venue: String,
    val startsAt: String
)