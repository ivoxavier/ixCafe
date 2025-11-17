package com.ixsvf.ixcafe.services.repository.remote.endpoints

import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.services.repository.model.IxCafeLoginResponse
import com.ixsvf.ixcafe.services.repository.model.ListaEmpregadosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface IxCafeApiInterface {

    @GET(IxCafeConstants.ENDPOINTSROUTES.LISTAR_EMPREGADOS)
    suspend fun listarEmpregados(): List<ListaEmpregadosResponse>


    @GET(IxCafeConstants.ENDPOINTSROUTES.LOGIN)
    suspend fun login(
        @Query("nome") nome: String,
        @Query("pin") pin: String,
        @Query("token_role") tokenRole: Int
    ): IxCafeLoginResponse
}