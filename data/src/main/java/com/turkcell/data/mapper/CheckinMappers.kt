package com.turkcell.data.mapper

import com.turkcell.core.domain.checkin.ScanResult
import com.turkcell.data.dto.checkin.ScanResponseDto

internal fun ScanResponseDto.toDomain(): ScanResult = ScanResult(
    ticketId = ticketId,
    ticketType = ticketType,
    eventName = event.name,
    venue = event.venue,
    startsAt = event.startsAt,
    checkedInAt = checkedInAt
)