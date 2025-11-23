package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "queue_orders")
data class QueueOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoJson: String, // O JSON completo pronto a enviar para a API
    val createdAt: Long = System.currentTimeMillis(), // Para garantir ordem de envio (FIFO)
    val retryCount: Int = 0 // Para gerir falhas repetidas
)