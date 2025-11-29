package com.ixsvf.ixcafe.services.repository.model

data class CartItem(
    val product: Produto,
    val quantity: Int,
    val observation: String? = null
)