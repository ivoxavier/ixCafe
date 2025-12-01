package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ixsvf.ixcafe.services.repository.model.Categoria
import com.ixsvf.ixcafe.services.repository.model.dto.CategoriaDto

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey val id: Int,
    val nome: String
)

// --- MAPPERS (CONVERSORES) ---

// 1. Converte da Base de Dados (Entity) para o Domínio (App)
// ESTA É A FUNÇÃO QUE ESTAVA A FALTAR OU MAL DEFINIDA
fun CategoriaEntity.toCategoria(): Categoria {
    return Categoria(
        id = this.id,
        name = this.nome
    )
}

// 2. Converte da API (DTO) para a Base de Dados (Entity)
fun CategoriaDto.toCategoriaEntity(): CategoriaEntity {
    return CategoriaEntity(
        id = this.id,
        nome = this.nome
    )
}