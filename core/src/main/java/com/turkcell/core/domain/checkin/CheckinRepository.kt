package com.turkcell.core.domain.checkin

interface CheckinRepository {
    suspend fun scanQr(qrCode: String): Result<ScanResult>
}