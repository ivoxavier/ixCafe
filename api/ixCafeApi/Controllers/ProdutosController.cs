using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Produtos;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class ProdutosController : ControllerBase{

        private readonly ProdutosRequests _produtosRequests = new ProdutosRequests();


        public ProdutosController(ProdutosRequests produtosRequests)
        {
            _produtosRequests = produtosRequests;
        }

        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/produtos/criar")]
        public async Task<IActionResult> Criar(ProdutosCriarRequest produtosCriarRequest)
        {
        
            var result = await _produtosRequests.Criar(produtosCriarRequest); 

            
            return Ok(result);
        }

    }
}