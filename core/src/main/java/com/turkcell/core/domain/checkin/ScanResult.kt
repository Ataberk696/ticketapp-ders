package com.turkcell.core.domain.checkin

data class ScanResult(
    val ticketId: String,
    val ticketType: String,
    val eventName: String,
    val venue: String,
    val startsAt: String,
    val checkedInAt: String
)