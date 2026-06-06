package com.turkcell.data.remote

import com.turkcell.data.dto.checkin.ScanRequestDto
import com.turkcell.data.dto.checkin.ScanResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckinApi {
    @POST("/checkin/scan")
    suspend fun scan(@Body body: ScanRequestDto): ScanResponseDto
}