package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile


@Entity(tableName = IxCafeConstants.LOCAL_DB.TABLES.EMPREGADOS)
data class EmpregadosEntity(
    @PrimaryKey val id: String, // O nome ou ID único do empregado
    val name: String,
    val role: String,
    val pinHash: String? = null // Preparado para o futuro (Login Offline)
)

// Função de extensão para converter Entidade -> Modelo de UI
fun EmpregadosEntity.toEmpregadosProfile(): EmpregadosProfile {
    return EmpregadosProfile(
        id = this.id.toString(),
        name = this.name,
        role = this.role
    )
}

// Função de extensão para converter Modelo de UI -> Entidade
fun EmpregadosProfile.toEmpregadosEntity(): EmpregadosEntity {
    return EmpregadosEntity(
        id = this.id,
        name = this.name,
        role = this.role
    )
}