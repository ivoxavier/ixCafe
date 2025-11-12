package com.ixsvf.ixcafe.services.repository.model

import kotlinx.serialization.Serializable

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
