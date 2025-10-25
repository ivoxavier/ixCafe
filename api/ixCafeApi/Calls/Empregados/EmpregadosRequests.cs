using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;
using Microsoft.AspNetCore.Http.HttpResults;
namespace ixCafeApi.Calls.Empregados
{
    public class EmpregadosRequests
    {
        private readonly string _connectionString;

        public EmpregadosRequests(IConfiguration configuration)
        {
            _connectionString = configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' não encontrada.");
        }

        public async Task<object> Criar(EmpregadoCriarRequest empregadoCriarRequest)
        {

            string pinHash = BCrypt.Net.BCrypt.HashPassword(empregadoCriarRequest.PinAcesso);


            var parameters = new DynamicParameters();

            parameters.Add("p_nome", empregadoCriarRequest.Nome);
            parameters.Add("p_pin_acesso", pinHash);
            parameters.Add("p_cargo", empregadoCriarRequest.Cargo);
            parameters.Add("p_token_role", empregadoCriarRequest.TokenRole);


            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);



            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_CriarEmpregado",
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

            return new { Message = "Acessos criados com sucesso!" };
        }
   
   
        

        public async Task<object> Desativar(EmpregadoDesativarRequest empregadoDesativarRequest)
        {
            return new { Message = "Desativo!" };
        }
    }
}