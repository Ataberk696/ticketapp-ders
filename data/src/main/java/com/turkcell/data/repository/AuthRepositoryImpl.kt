package com.turkcell.data.repository

import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.AuthSession
import com.turkcell.core.domain.auth.User
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.data.dto.auth.CredentialsDto
import com.turkcell.data.local.TokenStore
import com.turkcell.data.remote.AuthApi
import com.turkcell.data.util.runCatchingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { it != null }


    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email=email, password=password))
    }.onSuccess {
        tokenStore.save(it.accessToken, it.refreshToken)
    }
    .map {
        TokenPairDto -> AuthSession(
        user = User(
            TokenPairDto.user.id,  TokenPairDto.user.email, UserRole.fromApi( TokenPairDto.user.role),
        ),
        accessToken =  TokenPairDto.accessToken,
        refreshToken =  TokenPairDto.refreshToken)
    }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession>  = runCatchingApi {
        authApi.register(CredentialsDto(email=email, password=password))
    }
        .onSuccess {
            // jwt kaydet.
        }
        .map {
            i -> AuthSession(
                user = User(
                    id = i.user.id,
                    email = i.user.email,
                    role = UserRole.fromApi(i.user.role)
                ),
                accessToken = i.accessToken,
                refreshToken = i.refreshToken
            )
        }


    override suspend fun logout(): Result<Unit> {
        return try {
            tokenStore.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}