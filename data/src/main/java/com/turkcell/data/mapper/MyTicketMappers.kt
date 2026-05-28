package com.turkcell.data.mapper

import com.turkcell.core.domain.purchase.MyTicket
import com.turkcell.core.domain.purchase.TicketStatus
import com.turkcell.data.dto.purchase.MyTicketDto

internal fun MyTicketDto.toDomain(): MyTicket = MyTicket(
    id = id,
    qrCode = qrCode,
    status = TicketStatus.fromApi(status),
    ticketTypeName = ticketType.name,
    eventName = ticketType.event.name,
    eventVenue = ticketType.event.place,
    eventStartsAt = ticketType.event.startsAt
)