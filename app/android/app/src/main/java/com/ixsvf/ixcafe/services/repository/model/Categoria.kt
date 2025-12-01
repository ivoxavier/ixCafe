package com.ixsvf.ixcafe.services.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class Categoria(
    val id: Int,
    val name: String
)