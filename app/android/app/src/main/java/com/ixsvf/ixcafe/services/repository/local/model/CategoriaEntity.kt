package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ixsvf.ixcafe.services.repository.model.dto.CategoriaDto

// Definimos a tabela "categorias"
@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey val id: Int,
    val nome: String
)

// --- MAPPERS ---

// Converte DTO (da API) para Entidade (Room)
fun CategoriaDto.toEntity(): CategoriaEntity {
    return CategoriaEntity(
        id = this.id,
        nome = this.nome
    )
}

// Para converter Entidade para Modelo de Domínio,
// precisaremos criar o modelo 'Categoria' se ainda não existir,
// ou usar o DTO/outro objeto na UI.
// Se usar o CategoriaDto na UI (simplificação), pode adicionar este mapper:

fun CategoriaEntity.toCategoriaDto(): CategoriaDto {
    return CategoriaDto(
        id = this.id,
        nome = this.nome
    )
}