using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Produtos;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class ProdutosController : ControllerBase{

        private readonly ProdutosRequests _produtosRequests;


        public ProdutosController(ProdutosRequests produtosRequests)
        {
            _produtosRequests = produtosRequests;
        }


        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/produtos/novacategoria")]
        public async Task<IActionResult> NovaCategoria(string categoria)
        {

            var result = await _produtosRequests.NovaCategoria(categoria); 
            

            if(result is ErrorResponse error)
            {
                return Conflict(error);
            }

            
            return Ok(result);
        }


        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/produtos/criar")]
        public async Task<IActionResult> Criar(ProdutosCriarRequest produtosCriarRequest)
        {

            var result = await _produtosRequests.Criar(produtosCriarRequest);


            if(result is ErrorResponse error)
            {
                return Conflict(error);
            }


            return Ok(result);
        }
        

        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPut]
        [Route("api/produtos/disponibilidade")]
        public async Task<IActionResult> Disponibilidade(string nome, bool disponibilidade)
        {

            var result = await _produtosRequests.Disponibilidade(nome,disponibilidade); 
            

            if(result is ErrorResponse error)
            {
                return Conflict(error);
            }

            
            return Ok(result);
        }

    }
}