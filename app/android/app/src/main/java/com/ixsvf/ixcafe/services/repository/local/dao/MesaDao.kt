package com.ixsvf.ixcafe.services.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ixsvf.ixcafe.services.repository.local.model.MesaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MesaDao {

    // 1. Obter todas as mesas (Observável)
    // Ordenamos pelo número da mesa para a grelha aparecer organizada (1, 2, 3...)
    // Nota: Como 'number' é String, a ordenação será alfabética (1, 10, 2).
    // Se quiser numérica, teremos de ajustar a query ou converter para Int na Entity.
    @Query("SELECT * FROM mesas ORDER BY number ASC")
    fun getAllMesas(): Flow<List<MesaEntity>>

    // 2. Obter uma mesa específica (útil para validar antes de abrir pedido)
    @Query("SELECT * FROM mesas WHERE id = :id")
    suspend fun getMesaById(id: Int): MesaEntity?

    // 3. Obter mesas por zona/localização (Ex: "Esplanada")
    @Query("SELECT * FROM mesas WHERE location = :localizacao")
    fun getMesasByLocation(localizacao: String): Flow<List<MesaEntity>>

    // 4. Inserir ou Atualizar (Sync da API)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mesas: List<MesaEntity>)

    // 5. Atualizar apenas o estado de uma mesa (Otimista UI Update)
    // Útil para marcar como "Ocupada" imediatamente no telemóvel antes da API responder
    @Query("UPDATE mesas SET status = :novoEstado WHERE id = :id")
    suspend fun updateEstadoMesa(id: Int, novoEstado: String)

    // 6. Limpar tudo
    @Query("DELETE FROM mesas")
    suspend fun clearAll()

    // 7. Transação de Sincronização Completa
    // Apaga as mesas locais antigas e insere as novas que vieram da API .NET
    @Transaction
    suspend fun replaceMesas(mesas: List<MesaEntity>) {
        clearAll()
        insertAll(mesas)
    }
}