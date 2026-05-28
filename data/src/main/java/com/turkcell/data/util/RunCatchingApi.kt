package com.turkcell.data.util

import com.turkcell.data.dto.ErrorResponseDto
import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> runCatchingApi(crossinline block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: HttpException) {
    // Sunucunun JSON hata gövdesini parse et
    val errorBody = e.response()?.errorBody()?.string()
    val serverCode = try {
        errorBody?.let {
            Json { ignoreUnknownKeys = true }.decodeFromString<ErrorResponseDto>(it).error.code
        }
    } catch (_: Exception) { null }

    Result.failure(
        ApiException(
            code = e.code(),
            errorMessage = serverCode ?: e.message(), // sunucu kodu yoksa HTTP mesajını kullan
            cause = e
        )
    )
} catch (e: IOException) {
    Result.failure(NetworkException(e))
} catch (e: Exception) {
    Result.failure(e)
}