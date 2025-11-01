using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;
using Microsoft.AspNetCore.Http.HttpResults;

namespace ixCafeApi.Calls.Mesas
{
    public class PedidoRequests
    {
        private readonly string _connectionString;

        public async Task<object> Pedido(PedidoRequest pedidoRequest)
        {
            
            await Task.Delay(100); 

            
            return new { Message = "Pedido criado com sucesso!" };
        }


        public async Task<object> Listar()
        {
            var parameters = new DynamicParameters();   
            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);


            IEnumerable<ListarPedidosResponse> listaPedidos;

            await using (var connection = new MySqlConnection(_connectionString))
            {

                listaPedidos = await connection.QueryAsync<ListarPedidosResponse>(
                    "sp_ListarEmpregado",
                    parameters,
                    commandType: CommandType.StoredProcedure
                );
            }

            int errorCode = parameters.Get<int>("perrorCode");
            

            if (errorCode != 0)
            {
                
                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = errorCode,
                    ErrorMessage = parameters.Get<string>("perrorMessage")
                };
                return errorResponse;
            }

            return listaPedidos;
        }
    }
}