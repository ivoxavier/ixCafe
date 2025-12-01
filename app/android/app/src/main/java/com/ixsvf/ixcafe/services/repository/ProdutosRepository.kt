package com.ixsvf.ixcafe.services.repository

import com.ixsvf.ixcafe.services.repository.local.dao.CategoriaDao
import com.ixsvf.ixcafe.services.repository.local.dao.ProdutoDao
import com.ixsvf.ixcafe.services.repository.local.model.toCategoria
import com.ixsvf.ixcafe.services.repository.local.model.toCategoriaEntity
import com.ixsvf.ixcafe.services.repository.local.model.toProduto
import com.ixsvf.ixcafe.services.repository.local.model.toProdutoEntity
import com.ixsvf.ixcafe.services.repository.model.Categoria
import com.ixsvf.ixcafe.services.repository.model.Produto
import com.ixsvf.ixcafe.services.repository.model.dto.ProdutoDto
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdutosRepository(
    private val produtoDao: ProdutoDao,
    private val categoriaDao: CategoriaDao,
    private val localApi: LocalApiInterface
) {

    val categorias: Flow<List<Categoria>> = categoriaDao.getAllCategorias()
        .map { entities -> entities.map { it.toCategoria() } }

    val todosProdutos: Flow<List<Produto>> = produtoDao.getAllProdutos()
        .map { entities -> entities.map { it.toProduto() } }

    suspend fun refreshTudo() {
        // 1. Obter Categorias da API
        val remoteCats = localApi.getCategorias()

        // Guardar Categorias no Room
        categoriaDao.updateCategorias(remoteCats.map { it.toCategoriaEntity() })

        // 2. Obter Produtos (Ciclo por cada categoria)
        val listaFinalProdutos = mutableListOf<ProdutoDto>()

        for (categoria in remoteCats) {
            try {
                println("!!! DEBUG: A pedir produtos da categoria ${categoria.id}...")

                // Chama a API com o ID da categoria (ex: ?categoria=1)
                val produtosDestaCategoria = localApi.getProdutosPorCategoria(categoria.id)

                listaFinalProdutos.addAll(produtosDestaCategoria)

            } catch (e: Exception) {
                // Se der erro numa categoria (ex: 404 ou vazia), ignora e continua para a próxima
                // DICA: Se a sua API retornar Error 99 para categorias vazias, este catch impede o crash.
                println("!!! AVISO: Falha ao obter produtos da categoria ${categoria.id}: ${e.message}")
            }
        }

        // 3. Guardar TODOS os produtos encontrados no Room
        if (listaFinalProdutos.isNotEmpty()) {
            produtoDao.updateProdutos(listaFinalProdutos.map { it.toProdutoEntity() })
            println("!!! SUCESSO: ${listaFinalProdutos.size} produtos guardados.")
        } else {
            println("!!! AVISO: Nenhum produto encontrado em nenhuma categoria.")
        }
    }

    // Alias para compatibilidade
    suspend fun refreshProdutos() = refreshTudo()
}