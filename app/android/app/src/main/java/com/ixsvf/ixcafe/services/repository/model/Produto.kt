package com.ixsvf.ixcafe.services.repository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    // Mapeamos os nomes da API/Base de dados (snake_case) para Kotlin (camelCase)
    @SerialName("id_produto") val id: Int,
    @SerialName("id_categoria") val categoryId: Int,
    @SerialName("nome") val name: String,
    @SerialName("descricao") val description: String? = null,
    @SerialName("preco") val price: Double,
    @SerialName("disponivel") val isAvailable: Boolean
)