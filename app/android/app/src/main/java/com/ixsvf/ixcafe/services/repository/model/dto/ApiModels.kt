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
    // O seu Swagger 'ListarProdutosResponse' só mostra nome, descricao e preco.
    // MAS, para isto funcionar, a sua API *TEM* de retornar também o ID e o ID da Categoria.
    // Vou assumir que vai ajustar a API para incluir 'id_produto' e 'id_categoria'.
    // Se não incluir, não conseguimos ligar o produto à categoria correta!

    @SerialName("id_produto") val id: Int = 0, // Ajuste na API necessário
    @SerialName("id_categoria") val categoryId: Int = 0, // Ajuste na API necessário
    @SerialName("nome") val nome: String,
    @SerialName("descricao") val descricao: String? = null,
    @SerialName("preco") val preco: Double
)


@Serializable
data class CategoriaDto(
    @SerialName("id_Categoria") val id: Int,
    @SerialName("nome") val nome: String
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



