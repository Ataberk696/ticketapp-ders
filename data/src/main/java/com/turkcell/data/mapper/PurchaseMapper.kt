package com.turkcell.data.mapper

import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseStatus
import com.turkcell.core.domain.purchase.Ticket
import com.turkcell.core.domain.purchase.TicketStatus
import com.turkcell.data.dto.purchase.PurchaseDto
import com.turkcell.data.dto.purchase.PurchaseItemDto
import com.turkcell.data.dto.purchase.TicketDto


internal fun PurchaseDto.toDomain(): Purchase = Purchase(
    id = id,
    userId = userId,
    status = PurchaseStatus.fromApi(status),
    totalCents = totalCents,
    createdAt = createdAt,
    paidAt = paidAt,
    items = items.map { it.toDomain() },
    tickets = tickets.map { it.toDomain() }
)

internal fun PurchaseItemDto.toDomain(): PurchaseItem = PurchaseItem(
    id = id,
    ticketTypeId = ticketTypeId,
    quantity = quantity,
    unitPriceCents = unitPriceCents
)

internal fun TicketDto.toDomain(): Ticket = Ticket(
    id = id,
    qrCode = qrCode,
    status = TicketStatus.fromApi(status),
    ticketTypeId = ticketTypeId
)