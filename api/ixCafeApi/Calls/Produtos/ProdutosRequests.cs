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

            parameters.Add("p_categoria", produtosCriarRequest.IdCategoria);
            parameters.Add("p_nome", produtosCriarRequest.Nome);
            parameters.Add("p_descricao", produtosCriarRequest.Descricao);
            parameters.Add("p_preco", produtosCriarRequest.Preco);


            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);


            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_criarProduto",
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


        public async Task<object> Disponibilidade(string nome, bool disponibilidade){
         var parameters = new DynamicParameters();

            parameters.Add("p_nome", nome);
            parameters.Add("p_disponivel", disponibilidade);


            parameters.Add("perrorCode", dbType: DbType.Int32, direction: ParameterDirection.Output);
            parameters.Add("perrorMessage", dbType: DbType.String, direction: ParameterDirection.Output, size: 255);

            await using (var connection = new MySqlConnection(_connectionString))
            {

                await connection.ExecuteAsync(
                    "sp_disponibilidade",
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
            return new { Message = "Disponibilidade atualizada com sucesso!" };
        }


        public async Task<object> Listar(int categoria)
        {
            var parameters = new DynamicParameters();

            parameters.Add("pCategoria", categoria);

            IEnumerable<ListarProdutosResponse> listarProdutosResponse;

            await using (var connection = new MySqlConnection(_connectionString))
            {

                listarProdutosResponse = await connection.QueryAsync<ListarProdutosResponse>(
                    "sp_ListarProdutos",
                    parameters,
                    commandType: CommandType.StoredProcedure
                );
            }

            if (listarProdutosResponse.Count() == 0)
            {

                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = 99,
                    ErrorMessage = "Sem Resultados!"
                };
                return errorResponse;
            }

            return listarProdutosResponse;
        }


        public async Task<object> ListarCategorias()
        {


            IEnumerable<ListarCategoriasResponse> listarCategoriasResponse;

            await using (var connection = new MySqlConnection(_connectionString))
            {

                listarCategoriasResponse = await connection.QueryAsync<ListarCategoriasResponse>(
                    "sp_ListarCategorias",
                    commandType: CommandType.StoredProcedure
                );
            }

            if (listarCategoriasResponse.Count() == 0)
            {

                ErrorResponse errorResponse = new ErrorResponse
                {
                    ErrorCode = 99,
                    ErrorMessage = "Sem Resultados!"
                };
                return errorResponse;
            }

            return listarCategoriasResponse;
        }

    }
}