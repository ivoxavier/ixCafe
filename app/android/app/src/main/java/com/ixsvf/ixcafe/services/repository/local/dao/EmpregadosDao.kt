package com.ixsvf.ixcafe.services.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ixsvf.ixcafe.services.repository.local.model.EmpregadosEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpregadosDao {

    // Retorna um Flow: sempre que a tabela mudar, a UI recebe a nova lista automaticamente
    @Query("SELECT * FROM empregados WHERE isActive = 1 ORDER BY name ASC")
    fun getAllEmpregados(): Flow<List<EmpregadosEntity>>


    // Insere uma lista de utilizadores. Se o ID já existir, substitui os dados.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<EmpregadosEntity>)


    @Query("SELECT * FROM empregados WHERE id = :id")
    suspend fun getEmpregadosById(id: String): EmpregadosEntity?


    @Query("DELETE FROM empregados")
    suspend fun clearAll()



    @Query("SELECT * FROM empregados WHERE id = :id")
    fun getEmpregadoFlow(id: String): Flow<EmpregadosEntity?>


    // Transação para atualizar a cache: limpa tudo e insere os novos
    @Transaction
    suspend fun updateEmpregados(users: List<EmpregadosEntity>) {
        clearAll()
        insertAll(users)
    }
}