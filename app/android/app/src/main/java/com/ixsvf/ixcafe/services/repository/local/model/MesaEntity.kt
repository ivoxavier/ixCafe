package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ixsvf.ixcafe.services.repository.model.Mesa

@Entity(tableName = "mesas")
data class MesaEntity(
    @PrimaryKey val id: Int,
    val number: String,
    val location: String?,
    val capacity: Int,
    val status: String // Guardamos o estado localmente para funcionar offline
)

// --- MAPPERS ---

fun MesaEntity.toMesa(): Mesa {
    return Mesa(
        id = this.id,
        number = this.number,
        location = this.location,
        capacity = this.capacity,
        status = this.status
    )
}

fun Mesa.toMesaEntity(): MesaEntity {
    return MesaEntity(
        id = this.id,
        number = this.number,
        location = this.location,
        capacity = this.capacity,
        status = this.status
    )
}