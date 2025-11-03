using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Pedido;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class PedidoController : ControllerBase{

        private readonly PedidoRequests _pedidoRequests;


        public PedidoController(PedidoRequests pedidoRequests)
        {
            _pedidoRequests = pedidoRequests;
        }

        //[ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        [HttpPost]
        [Route("api/pedido/pedir")]
        public async Task<IActionResult> Pedir(PedidoRequest pedidoRequest)
        {

            var result = await _pedidoRequests.Pedido(pedidoRequest);
            
            if(result is ErrorResponse error)
            {
                return Conflict(error);
            }
            
            return Ok(result);
        }

        [ProducesResponseType(typeof(ListarPedidosResponse), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet]
        [Route("api/pedidos/listar")]
        public async Task<IActionResult> Listar ()
        {
            var result = await _pedidoRequests.Listar();

            if(result is ErrorResponse error)
            {
                return NotFound(error);
            }


            return Ok(result);


        }

    }
}