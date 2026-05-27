package com.turkcell.core.domain.purchase

interface PurchaseRepository {
    suspend fun createPurchase(items: List<Pair<String, Int>>): Result<Purchase>
    suspend fun payPurchase(purchaseId: String): Result<Purchase>
    suspend fun getPurchase(purchaseId: String): Result<Purchase>
}