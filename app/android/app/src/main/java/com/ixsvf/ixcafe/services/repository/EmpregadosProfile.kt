package com.ixsvf.ixcafe.services.repository





/*o profile é usado ambos os metodos, local e remote*/



data class EmpregadosProfile(
    val id: String,
    val name: String,
    val role: String,
    val pinHash: String? = null,
    val isActive: Boolean = true
)


