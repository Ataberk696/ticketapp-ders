package com.turkcell.core.domain.purchase

enum class PurchaseStatus {
    PENDING, PAID;

    companion object {
        fun fromApi(value: String?): PurchaseStatus = when (value?.uppercase()){
            "PAID" -> PurchaseStatus.PAID
            else -> PurchaseStatus.PENDING
        }
    }
}