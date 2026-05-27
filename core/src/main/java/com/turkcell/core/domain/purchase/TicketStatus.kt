package com.turkcell.core.domain.purchase

enum class TicketStatus{
    VALID, USED;

    companion object {
        fun fromApi(value: String?): TicketStatus = when (value?.uppercase()){
            "USED" -> TicketStatus.USED
            else -> TicketStatus.VALID
        }
    }
}