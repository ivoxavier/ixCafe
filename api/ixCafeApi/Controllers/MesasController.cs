using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Mesas;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class MesasController : ControllerBase{

        private readonly MesasRequests _mesasRequests = new MesasRequests();


        public MesasController(MesasRequests mesasRequests)
        {
            _mesasRequests = mesasRequests;
        }

        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/mesas/criar")]
        public async Task<IActionResult> Criar(MesasCriarRequest mesasCriarRequest)
        {
        
            var result = await _mesasRequests.Criar(mesasCriarRequest); 

            
            return Ok(result);
        }

    }
}