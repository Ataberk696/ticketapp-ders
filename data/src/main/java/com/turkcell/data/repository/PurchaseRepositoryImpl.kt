package com.turkcell.data.repository

import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.data.dto.purchase.CreatePurchaseRequestDto
import com.turkcell.data.dto.purchase.PurchaseItemRequestDto
import com.turkcell.data.mapper.toDomain
import com.turkcell.data.remote.PurchaseApi
import com.turkcell.data.util.runCatchingApi

class PurchaseRepositoryImpl(
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {

    override suspend fun createPurchase(
        items: List<Pair<String, Int>>
    ): Result<Purchase> = runCatchingApi {
        val requestItems = items.map { (ticketTypeId, quantity) ->
            PurchaseItemRequestDto(ticketTypeId = ticketTypeId, quantity = quantity)
        }
        purchaseApi.createPurchase(CreatePurchaseRequestDto(items = requestItems))
    }.map { it.toDomain() }

    override suspend fun payPurchase(purchaseId: String): Result<Purchase> =
        runCatchingApi { purchaseApi.payPurchase(purchaseId) }.map { it.toDomain() }

    override suspend fun getPurchase(purchaseId: String): Result<Purchase> =
        runCatchingApi { purchaseApi.getPurchase(purchaseId) }.map { it.toDomain() }

}