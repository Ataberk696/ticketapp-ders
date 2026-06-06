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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) : AuthRepository {
    override val isLoggedIn: Flow<Boolean> = tokenStore.accessToken.map { it != null }

    override val currentUser: Flow<User?> = combine(
        tokenStore.userId,
        tokenStore.userEmail,
        tokenStore.userRole
    ) { id, email, role ->
        if (id != null && email != null && role != null)
            User(id, email, UserRole.fromApi(role))
        else
            null
    }


    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthSession> = runCatchingApi {
        authApi.login(CredentialsDto(email=email, password=password))
    }.onSuccess { tokenPair->
        tokenStore.saveAll(
            access = tokenPair.accessToken,
            refresh = tokenPair.refreshToken,
            userId = tokenPair.user.id,
            userEmail = tokenPair.user.email,
            userRole = tokenPair.user.role
        )
    }
    .map { tokenPairDto ->
        AuthSession(
            user = User(
                id = tokenPairDto.user.id,
                email = tokenPairDto.user.email,
                role = UserRole.fromApi(tokenPairDto.user.role)
            ),
            accessToken = tokenPairDto.accessToken,
            refreshToken = tokenPairDto.refreshToken
        )
    }


    override suspend fun register(
        email: String,
        password: String
    ): Result<AuthSession>  = runCatchingApi {
        authApi.register(CredentialsDto(email=email, password=password))
    }
        .onSuccess { tokenPair ->
            tokenStore.saveAll(
                access = tokenPair.accessToken,
                refresh = tokenPair.refreshToken,
                userId = tokenPair.user.id,
                userEmail = tokenPair.user.email,
                userRole = tokenPair.user.role
            )
        }
        .map { tokenPairDto ->
            AuthSession(
                user = User(
                    id = tokenPairDto.user.id,
                    email = tokenPairDto.user.email,
                    role = UserRole.fromApi(tokenPairDto.user.role)
                ),
                accessToken = tokenPairDto.accessToken,
                refreshToken = tokenPairDto.refreshToken
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