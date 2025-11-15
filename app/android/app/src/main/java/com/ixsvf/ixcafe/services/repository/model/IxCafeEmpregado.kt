package com.ixsvf.ixcafe.services.repository.model

import kotlinx.serialization.Serializable




data class UserProfile(
    val name: String,
    val role: String
)


//Body RestAPi
@Serializable
data class IxCafeLoginRequest(
    val nome: String,
    val pin: String,
    val token_role: Int
)


@Serializable
data class IxCafeLoginResponse(
    val nome: String,
    val idEmpregado: Int
)

@Serializable
data class ListaEmpregadosResponse(
    val nome: String,
    val cargo: String
)
