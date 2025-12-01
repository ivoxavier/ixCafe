package com.ixsvf.ixcafe.services.repository.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ixsvf.ixcafe.services.repository.model.Produto
import com.ixsvf.ixcafe.services.repository.model.dto.ProdutoDto

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey val id: Int,
    val categoryId: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val isAvailable: Boolean
)


fun ProdutoEntity.toProduto(): Produto {
    return Produto(
        id = this.id,
        categoryId = this.categoryId,
        name = this.name,
        description = this.description,
        price = this.price,
        isAvailable = this.isAvailable
    )
}

// --- CORREÇÃO: Nome explícito 'toProdutoEntity' ---
fun ProdutoDto.toProdutoEntity(): ProdutoEntity {
    return ProdutoEntity(
        id = this.id,
        categoryId = this.categoryId, // ou idCategoria, dependendo do DTO
        name = this.nome,
        description = this.descricao,
        price = this.preco,
        isAvailable = true
    )
}