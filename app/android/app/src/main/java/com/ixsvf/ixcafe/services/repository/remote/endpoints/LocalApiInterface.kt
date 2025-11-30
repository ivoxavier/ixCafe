package com.ixsvf.ixcafe.services.repository.remote.endpoints

import com.ixsvf.ixcafe.services.repository.model.dto.CategoriaDto
import com.ixsvf.ixcafe.services.repository.model.dto.MesaDto
import com.ixsvf.ixcafe.services.repository.model.dto.MesaRequest
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoRequest
import com.ixsvf.ixcafe.services.repository.model.dto.ProdutoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LocalApiInterface {

    // Swagger: /api/mesas/listar
    @GET("/api/mesas/listar")
    suspend fun getMesas(): List<MesaDto>

    // Swagger: /api/produtos/listar (Retorna TODOS se não passar categoria, ou filtrados)
    // Idealmente, pedimos todos de uma vez para guardar cache local.
    @GET("api/produtos/listar")
    suspend fun getProdutos(): List<ProdutoDto>

    // Swagger: /api/pedido/pedir
    @POST("/api/pedido/pedir")
    suspend fun criarPedido(@Body pedido: PedidoRequest)

    @POST("api/mesas/criar")
    suspend fun criarMesa(@Body mesa: MesaRequest): Response<Unit>


    // Swagger: /api/produtos/listarcategorias
    @GET("api/produtos/listarcategorias")
    suspend fun getCategorias(): List<CategoriaDto>


}