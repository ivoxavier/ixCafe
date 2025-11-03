using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;
using Microsoft.AspNetCore.Http.HttpResults;

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
            String? pedido_serialized = pedidoRequest.ToString();



            var parameters = new DynamicParameters();    


            parameters.Add("p_pedido_jsoned", pedido_serialized);
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

                return parameters.Get<long>("pnPedido");
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