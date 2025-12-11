package com.ixsvf.ixcafe.services.repository

import com.ixsvf.ixcafe.services.repository.local.dao.MesaDao
import com.ixsvf.ixcafe.services.repository.local.model.MesaEntity
import com.ixsvf.ixcafe.services.repository.local.model.toMesa
import com.ixsvf.ixcafe.services.repository.local.model.toMesaEntity
import com.ixsvf.ixcafe.services.repository.model.Mesa
import com.ixsvf.ixcafe.services.repository.model.dto.MesaRequest
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MesasRepository(
    private val mesaDao: MesaDao,
    private val localApi: LocalApiInterface // A interface Retrofit para a API .NET
) {

    // --- 1. FONTE DE VERDADE (Offline-First) ---
    // A UI observa este Flow. Ele emite dados imediatamente do disco (Room).
    val mesas: Flow<List<Mesa>> = mesaDao.getAllMesas()
        .map { entities ->
            entities.map { it.toMesa() }
        }

    // --- 2. OBTER MESA ESPECÍFICA ---
    // Útil para o cabeçalho do ecrã de pedidos ("Mesa 5")
    suspend fun getMesaById(id: Int): Mesa? {
        return mesaDao.getMesaById(id)?.toMesa()
    }

    // --- 3. SINCRONIZAÇÃO (API .NET -> Room) ---
    // O ViewModel chama isto. Se falhar (sem rede), lança erro,
    // mas o Flow 'mesas' acima continua a mostrar o último estado conhecido.
    suspend fun refreshMesas() {
        val remoteMesas = localApi.getMesas()

        val entities = remoteMesas.map { dto ->
            MesaEntity(
                // O Swagger não retorna ID unico, usa o numero como chave?
                // Se o SQL tem id_mesa, a API devia retorná-lo.
                // Por agora, usamos o numero como ID (se for unico)
                id = dto.id,
                number = dto.numero.toString(),
                location = dto.localizacao,
                capacity = dto.capacidade,
                status = "Livre" // A API atual não retorna estado, assumimos Livre
            )
        }
        mesaDao.replaceMesas(entities)
    }

    // --- 4. ATUALIZAÇÃO OTIMISTA (Opcional) ---
    // Se quiser marcar uma mesa como "Ocupada" instantaneamente ao entrar no pedido,
    // antes mesmo de enviar para a API.
    suspend fun marcarMesaComoOcupada(id: Int) {
        mesaDao.updateEstadoMesa(id, "Ocupada")
    }


    suspend fun criarMesa(numero: Int, localizacao: String, capacidade: Int) {
        val request = MesaRequest(numero, localizacao, capacidade)
        val response = localApi.criarMesa(request)

        if (response.isSuccessful) {
            refreshMesas() // Atualiza a lista local
        } else {
            throw Exception("Erro API: ${response.code()}")
        }
    }

    suspend fun editarMesa(id: Int, numero: Int, localizacao: String, capacidade: Int) {
        val request = MesaRequest(numero, localizacao, capacidade)
        val response = localApi.editarMesa(id, request)

        if (response.isSuccessful) {
            refreshMesas()
        } else {
            throw Exception("Erro API: ${response.code()}")
        }
    }

    suspend fun apagarMesa(id: Int) {
        val response = localApi.apagarMesa(id)
        if (response.isSuccessful) {
            //mesaDao.deleteById(id) // Apaga localmente
            refreshMesas() // Garante sincronia
        }
    }

}