package com.ixsvf.ixcafe.services.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ixsvf.ixcafe.services.repository.local.model.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    // 1. Obter todas as categorias (reativo com Flow)
    // Ordenamos por nome para a lista aparecer alfabética
    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    // 2. Inserir ou Atualizar uma lista de categorias
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<CategoriaEntity>)

    // 3. Limpar a tabela de categorias
    @Query("DELETE FROM categorias")
    suspend fun clearAll()

    // 4. Transação de Sincronização (Sync)
    // Apaga as antigas e insere as novas que vieram da API
    @Transaction
    suspend fun updateCategorias(categorias: List<CategoriaEntity>) {
        clearAll()
        insertAll(categorias)
    }
}