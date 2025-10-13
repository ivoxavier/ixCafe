using ixCafeApi.Models;

namespace ixCafeApi.Calls.Produtos
{
    public class ProdutosRequests
    {
        public async Task<object> Criar(ProdutosCriarRequest produtosCriarRequest)
        {
            
            await Task.Delay(100); 

            
            return new { Message = "Produto criado com sucesso!" };
        }
    }
}