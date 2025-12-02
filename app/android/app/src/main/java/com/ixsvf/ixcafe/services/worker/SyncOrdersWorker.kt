package com.ixsvf.ixcafe.services.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ixsvf.ixcafe.IxCafeApplication
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoRequest
import com.ixsvf.ixcafe.services.repository.remote.RetrofitClient
import com.ixsvf.ixcafe.services.repository.remote.endpoints.LocalApiInterface
import kotlinx.serialization.json.Json

class SyncOrdersWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        println("!!! WORKER: A iniciar verificação de pedidos pendentes...")
        // 1. Obter acesso à BD e à API
        val database = (applicationContext as IxCafeApplication).database
        val queueDao = database.queueOrderDao()

        // Aqui usamos a API Local (.NET), não o Firebase
        val api = RetrofitClient.create(LocalApiInterface::class.java)

        // 2. Ler pedidos pendentes
        val pendingOrders = queueDao.getAllPendingOrders()

        if (pendingOrders.isEmpty()) {
            println("!!! WORKER: Nenhum pedido pendente.")
            return Result.success()
        }

        println("!!! WORKER: Encontrados ${pendingOrders.size} pedidos para enviar.") // <--- LOG
        return try {
            for (orderEntity in pendingOrders) {
                println("!!! WORKER: A enviar pedido ID ${orderEntity.id}...")

                // 3. Converter o JSON guardado de volta para Objeto
                val request = Json.decodeFromString<PedidoRequest>(orderEntity.pedidoJson)

                // 4. Enviar para a API .NET
                // (Assumindo que o método na interface lança exceção se falhar
                // ou retorna Response que podemos verificar isSuccessful)
                api.criarPedido(request)

                // 5. Se chegou aqui (sucesso), apaga da fila local
                queueDao.deleteOrder(orderEntity.id)
                println("!!! WORKER: Pedido ${orderEntity.id} enviado e apagado com SUCESSO!") // <--- LOG
            }

            Result.success()

        } catch (e: Exception) {
            println("Erro no SyncOrdersWorker: ${e.message}")
            // Se falhar (ex: servidor em baixo), tenta de novo mais tarde
            // O 'runAttemptCount' evita loops infinitos
            if (runAttemptCount > 3) {
                return Result.failure()
            }
            return Result.retry()
        }
    }
}