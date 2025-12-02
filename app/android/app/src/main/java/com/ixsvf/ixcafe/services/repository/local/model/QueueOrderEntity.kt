package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "queue_orders")
data class QueueOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)