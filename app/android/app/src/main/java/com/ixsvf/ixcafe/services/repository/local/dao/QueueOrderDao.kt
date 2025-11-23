package com.ixsvf.ixcafe.services.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ixsvf.ixcafe.services.repository.local.model.QueueOrderEntity


@Dao
interface QueueOrderDao {

    // 1. Inserir um novo pedido na fila (Chamado quando clica em "Confirmar" no UI)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: QueueOrderEntity): Long

    // 2. Obter todos os pedidos pendentes (Chamado pelo WorkManager)
    // Ordenamos por 'createdAt ASC' para enviar os pedidos mais antigos primeiro
    @Query("SELECT * FROM queue_orders ORDER BY createdAt ASC")
    suspend fun getAllPendingOrders(): List<QueueOrderEntity>

    // 3. Apagar um pedido específico (Chamado após sucesso da API)
    @Query("DELETE FROM queue_orders WHERE id = :id")
    suspend fun deleteOrder(id: Long)

    // 4. Incrementar contador de tentativas (Opcional, para lógica de retry robusta)
    @Query("UPDATE queue_orders SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetryCount(id: Long)

    // 5. Contar quantos estão pendentes (Útil para mostrar um indicador na UI "X por enviar")
    @Query("SELECT COUNT(*) FROM queue_orders")
    fun getPendingCount(): kotlinx.coroutines.flow.Flow<Int>
}