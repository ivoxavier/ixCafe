using ixCafeApi.Models;
using Dapper;
using MySqlConnector; 
using System.Data;

namespace ixCafeApi.Calls.Produtos
{
    public class ProdutosRequests
    {

        private readonly string _connectionString;

        public ProdutosRequests(IConfiguration configuration)
        {
            _connectionString = configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' não encontrada.");
        }

        public async Task<object> Criar(ProdutosCriarRequest produtosCriarRequest)
        {

            var parameters = new DynamicParameters();


            return new { Message = "Produto criado com sucesso!" };
        }


        public async Task<object> NovaCategoria(string categoria)
        {

            var parameters = new DynamicParameters();

            parameters.Add("p_categoria", categoria);


            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_NovaCategoria",
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
            return new { Message = "Categoria criada com sucesso!" };
        }

    }
}