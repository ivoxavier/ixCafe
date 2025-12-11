package com.ixsvf.ixcafe.services.repository.remote.endpoints

import com.ixsvf.ixcafe.services.repository.model.ProductRequest
import com.ixsvf.ixcafe.services.repository.model.dto.CategoriaDto
import com.ixsvf.ixcafe.services.repository.model.dto.MesaDto
import com.ixsvf.ixcafe.services.repository.model.dto.MesaRequest
import com.ixsvf.ixcafe.services.repository.model.dto.PedidoRequest
import com.ixsvf.ixcafe.services.repository.model.dto.ProdutoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LocalApiInterface {

    // Swagger: /api/mesas/listar
    @GET("/api/mesas/listar")
    suspend fun getMesas(): List<MesaDto>

    // Swagger: /api/produtos/listar (Retorna TODOS se não passar categoria, ou filtrados)
    // Idealmente, pedimos todos de uma vez para guardar cache local.
    // --- CORREÇÃO AQUI: Adicionado o parâmetro @Query ---
    // Isto transforma a chamada em: .../listar?categoria=1
    @GET("api/produtos/listar")
    suspend fun getProdutosPorCategoria(@Query("categoria") categoriaId: Int): List<ProdutoDto>

    // Swagger: /api/pedido/pedir
    @POST("/api/pedido/pedir")
    suspend fun criarPedido(@Body pedido: PedidoRequest)

    @POST("api/mesas/criar")
    suspend fun criarMesa(@Body mesa: MesaRequest): Response<Unit>


    // Swagger: /api/produtos/listarcategorias
    @GET("api/produtos/listarcategorias")
    suspend fun getCategorias(): List<CategoriaDto>


    // Assumindo que vai criar este endpoint na API .NET:
    @PUT("api/mesas/editar/{id}")
    suspend fun editarMesa(@Path("id") id: Int, @Body mesa: MesaRequest): Response<Unit>

    // Opcional: Apagar
    @DELETE("api/mesas/apagar/{id}")
    suspend fun apagarMesa(@Path("id") id: Int): Response<Unit>




    @POST("api/products") // Confirme se a rota no .NET é "api/products" ou só "products"
    suspend fun createProduct(@Body product: ProductRequest): Response<Unit>
}