package com.ixsvf.ixcafe.services.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ixsvf.ixcafe.services.repository.local.model.ProdutoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    // 1. Obter todos os produtos (útil para cache ou debug)
    @Query("SELECT * FROM produtos ORDER BY name ASC")
    fun getAllProdutos(): Flow<List<ProdutoEntity>>

    // 2. Obter produtos de uma categoria específica (para o ecrã de pedidos)
    // Filtramos também por 'isAvailable = 1' para não mostrar produtos indisponíveis no menu
    @Query("SELECT * FROM produtos WHERE categoryId = :categoryId AND isAvailable = 1 ORDER BY name ASC")
    fun getProdutosPorCategoria(categoryId: Int): Flow<List<ProdutoEntity>>

    // 3. Obter um produto específico pelo ID (útil para validar stock ou detalhes)
    @Query("SELECT * FROM produtos WHERE id = :produtoId")
    suspend fun getProdutoById(produtoId: Int): ProdutoEntity?

    // 4. Inserir ou Atualizar lista de produtos (Sync com API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(produtos: List<ProdutoEntity>)

    // 5. Limpar tabela (Cuidado ao usar)
    @Query("DELETE FROM produtos")
    suspend fun clearAll()

    // 6. Transação de Sincronização: Apaga antigos e insere novos da API
    @Transaction
    suspend fun updateProdutos(produtos: List<ProdutoEntity>) {
        clearAll()
        insertAll(produtos)
    }
}