using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;
using Microsoft.AspNetCore.Http.HttpResults;

namespace ixCafeApi.Calls.Mesas
{
    public class MesasRequests
    {

        private readonly string _connectionString;

        public MesasRequests(IConfiguration configuration)
        {
            _connectionString = configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' não encontrada.");
        }


        public async Task<object> Criar(MesasCriarRequest mesasCriarRequest)
        {
            
            var parameters = new DynamicParameters();

            parameters.Add("p_numero_mesa", mesasCriarRequest.NumeroMesa);
            parameters.Add("p_localizacao", mesasCriarRequest.Localizacao);
            parameters.Add("p_capacidade", mesasCriarRequest.Capacidade);
            
            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);
            
            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_CriarMesa",
                    parameters,
                    commandType: CommandType.StoredProcedure
                );
            }

            var errorCode = parameters.Get<int>("perrorCode");

            if (errorCode != 0)
            {
                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = errorCode,
                    ErrorMessage = parameters.Get<string>("perrorMessage")
                };
                return errorResponse;
            }

            return new { Message = "Mesa criada com sucesso!" };
        }
    }
}