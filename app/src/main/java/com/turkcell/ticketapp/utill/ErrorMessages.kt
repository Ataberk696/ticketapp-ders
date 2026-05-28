package com.turkcell.ticketapp.util

import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> when (code) {
        401 -> "Email veya şifre hatalı"
        403 -> when (errorMessage) {
            "not_purchase_owner" -> "Bu satın alma size ait değil"
            else -> "Bu işlem için yetkiniz yok"
        }
        404 -> "Kayıt bulunamadı"
        409 -> when (errorMessage) {
            "email_taken" -> "Bu email zaten kayıtlı"
            "capacity_exceeded" -> "Stok yetersiz, lütfen etkinliği yenileyin"
            "already_paid" -> "Bu satın alma zaten ödenmiş"
            else -> "İşlem şu anda yapılamıyor"
        }
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu"
}