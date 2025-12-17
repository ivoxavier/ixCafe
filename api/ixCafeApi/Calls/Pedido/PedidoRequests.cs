using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;
using Microsoft.AspNetCore.Http.HttpResults;
using System.Text.Json;

namespace ixCafeApi.Calls.Pedido
{
    public class PedidoRequests
    {
        private readonly string _connectionString;

        public PedidoRequests(IConfiguration configuration)
        {
            _connectionString = configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' não encontrada.");
        }

        public async Task<object> Pedido(PedidoRequest pedidoRequest)
        {
            var jsonOptions = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };

           string pedido_serialized = JsonSerializer.Serialize(pedidoRequest, jsonOptions);

            var parameters = new DynamicParameters();    


            parameters.Add("p_pedido", pedido_serialized,DbType.String);
            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);
            parameters.Add("pnPedido", dbType: DbType.Int32, direction: ParameterDirection.Output);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_novoPedido",
                    parameters,
                    commandType: CommandType.StoredProcedure
                );
            }

            int? errorCode = parameters.Get<int?>("perrorCode");
            string? errorMessage = parameters.Get<string?>("perrorMessage");

            if (errorCode.HasValue && errorCode.Value != 0)
            {

                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = errorCode.Value,
                    ErrorMessage = errorMessage ?? "Erro desconhecido na base de dados."
                };

                return errorResponse;
            }

                return new { success = true, message = "Pedido registado com sucesso" };
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
                    "sp_ListarPedidos",
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