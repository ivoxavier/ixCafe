using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Mesas;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class PedidoController : ControllerBase{

        private readonly PedidoRequests _pedidoRequests;


        public PedidoController(PedidoRequests pedidoRequests)
        {
            _pedidoRequests = pedidoRequests;
        }

        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/pedido/pedir")]
        public async Task<IActionResult> Pedir(PedidoRequest pedidoRequest)
        {
        
            var result = await _pedidoRequests.Pedido(pedidoRequest);

            
            return Ok(result);
        }

    }
}