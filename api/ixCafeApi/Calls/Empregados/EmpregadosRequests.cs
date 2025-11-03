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
            var parameters = new DynamicParameters();

            parameters.Add("p_nome", empregadoDesativarRequest.Nome);
            parameters.Add("p_token_role", empregadoDesativarRequest.TokenRole);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_DesativarEmpregado",
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

            return new { Message = "Desativo!" };
        }
        

        public async Task<object> Eliminar(string nomeEmpregado, int token_role)
        {
            var parameters = new DynamicParameters();

            parameters.Add("p_nome", nomeEmpregado);
            parameters.Add("p_token_role", token_role);
            
            
            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_EliminarEmpregado",
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
            return new { Message = "Empregado eliminado com sucesso!" };
        }



         public async Task<object> Login(string nomeEmpregado, string pin,int token_role)
        {
            
            
            
            var parameters = new DynamicParameters();




            parameters.Add("p_nome", nomeEmpregado);
            parameters.Add("p_token_role", token_role);


            parameters.Add("pidEmpregado", dbType: DbType.Int64, direction: ParameterDirection.Output);
            parameters.Add("pPinAcesso", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);
            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_login",
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

        
            string storedHash = parameters.Get<string>("pPinAcesso");

   
            bool isPinValid = BCrypt.Net.BCrypt.Verify(pin, storedHash);

            if (!isPinValid)
            {
                
                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = 98, 
                    ErrorMessage = "Erro: Credenciais inválidas"
                };
                return errorResponse;
            }

            LoginResponse sucessResponse = new LoginResponse
            {
                IdEmpregado = parameters.Get<long>("pidEmpregado"),
                Nome = nomeEmpregado
            };

            return sucessResponse;
        }










        public async Task<object> Listar()
        {

            IEnumerable<ListaEmpregadosResponse> listaEmpregados;

            await using (var connection = new MySqlConnection(_connectionString))
            {

                listaEmpregados = await connection.QueryAsync<ListaEmpregadosResponse>(
                    "sp_ListarEmpregado",
                    commandType: CommandType.StoredProcedure
                );
            }




            if (listaEmpregados.Count() == 0)
            {

                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = 99,
                    ErrorMessage = "Sem Resultados!"
                };
                return errorResponse;
            }

            return listaEmpregados;
        }


    }
}