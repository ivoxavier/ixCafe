package com.ixsvf.ixcafe.services.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ixsvf.ixcafe.services.repository.local.dao.QueueOrderDao
import com.ixsvf.ixcafe.services.repository.local.model.QueueOrderEntity
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoItemRequest
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoRequest
import com.ixsvf.ixcafe.services.worker.SyncOrdersWorker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PedidosRepository(
    private val queueOrderDao: QueueOrderDao,
    private val context: Context
) {

    suspend fun confirmarPedido(
        idMesa: Int,
        idEmpregado: Int, // Vem do perfil logado
        itens: List<Pair<Int, Int>> // Lista de (IdProduto, Quantidade)
    ) {
        // 1. Preparar o Objeto DTO
        val pedidoItens = itens.map { (prodId, qtd) ->
            PedidoItemRequest(
                idProduto = prodId,
                quantidade = qtd,
                observacoes = null // Pode adicionar obs aqui se tiver
            )
        }

        val request = PedidoRequest(
            idMesa = idMesa,
            idEmpregado = idEmpregado,
            pedidos = pedidoItens
        )

        // 2. Serializar para JSON
        val jsonPedido = Json.encodeToString(request)

        // 3. Guardar na Base de Dados Local (Room) - Ação imediata
        queueOrderDao.insert(
            QueueOrderEntity(pedidoJson = jsonPedido)
        )

        // 4. Agendar Sincronização (WorkManager)
        // Isto diz ao Android: "Assim que tiveres internet, corre o SyncOrdersWorker"
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncOrdersWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}