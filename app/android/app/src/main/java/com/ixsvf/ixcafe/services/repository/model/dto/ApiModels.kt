package com.ixsvf.ixcafe.services.repository.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- RESPOSTAS (GET) ---

@Serializable
data class MesaDto(
    @SerialName("numeroMesa") val numero: Int,
    @SerialName("localizacao") val localizacao: String? = null,
    @SerialName("capacidade") val capacidade: Int
    // Nota: O Swagger não mostra "estado" na lista, mas seria útil adicionar na API .NET
)

@Serializable
data class ProdutoDto(
    // Assumindo a estrutura baseada no seu SQL, já que não está no Swagger o GET
    val id: Int,
    val nome: String,
    val descricao: String?,
    val preco: Double,
    val idCategoria: Int
)

// --- PEDIDOS (POST) ---

@Serializable
data class PedidoRequest(
    @SerialName("idEmpregado") val idEmpregado: Int,
    @SerialName("idMesa") val idMesa: Int,
    @SerialName("pedidos") val pedidos: List<PedidoItemRequest>
)

@Serializable
data class PedidoItemRequest(
    @SerialName("idProduto") val idProduto: Int,
    @SerialName("quantidade") val quantidade: Int,
    @SerialName("observacoes") val observacoes: String? = null
)


@Serializable
data class MesaRequest(
    @SerialName("numeroMesa") val numero: Int,
    @SerialName("localizacao") val localizacao: String,
    @SerialName("capacidade") val capacidade: Int
)