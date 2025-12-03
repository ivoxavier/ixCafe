package com.ixsvf.ixcafe.services.repository.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mesa(
    // Mapeamento exato com os nomes que vêm da API .NET (baseados no SQL)
    @SerialName("id_mesa") val id: Int,
    @SerialName("numeroMesa") val number: String, // SQL é Varchar
    @SerialName("localizacao") val location: String? = null,
    @SerialName("capacidade") val capacity: Int,

    // Este campo não está na tabela 'mesas' do SQL, mas a API .NET
    // deve enviá-lo (calculado se existe conta aberta ou não)
    @SerialName("estado") val status: String = "Livre" // "Livre" ou "Ocupada"
)