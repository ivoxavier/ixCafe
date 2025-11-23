package com.ixsvf.ixcafe.services.repository

import com.ixsvf.ixcafe.services.repository.local.dao.ProdutoDao
import com.ixsvf.ixcafe.services.repository.local.model.ProdutoEntity
import com.ixsvf.ixcafe.services.repository.local.model.toProduto
import com.ixsvf.ixcafe.services.repository.local.model.toProdutoEntity
import com.ixsvf.ixcafe.services.repository.model.Produto
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutosRepository(
    private val produtoDao: ProdutoDao,
    private val localApi: LocalApiInterface // A sua API .NET no Raspberry Pi
) {

    // --- 1. FONTE DE VERDADE (Room) ---
    // A UI observa isto. Filtramos logo por categoria e disponibilidade no DAO.
    fun getProdutosPorCategoria(categoryId: Int): Flow<List<Produto>> {
        return produtoDao.getProdutosPorCategoria(categoryId)
            .map { entities ->
                entities.map { it.toProduto() }
            }
    }

    // Para obter todos (se precisar para cache ou pesquisa global)
    val todosProdutos: Flow<List<Produto>> = produtoDao.getAllProdutos()
        .map { entities ->
            entities.map { it.toProduto() }
        }

    // --- 2. SINCRONIZAÇÃO (API .NET -> Room) ---
    suspend fun refreshProdutos() {
        val remoteProdutos = localApi.getProdutos()

        val entities = remoteProdutos.map { dto ->
            ProdutoEntity(
                id = dto.id,
                categoryId = dto.idCategoria,
                name = dto.nome,
                description = dto.descricao,
                price = dto.preco,
                isAvailable = true // Assumimos true se a API não enviar
            )
        }
        produtoDao.updateProdutos(entities)
    }
}