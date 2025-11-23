package com.ixsvf.ixcafe.services.repository.model

data class EmpregadosProfile(
    val id: String,
    val name: String,
    val role: String,
    val pinHash: String? = null,
    val isActive: Boolean = true
)