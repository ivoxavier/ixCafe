using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using ixCafeApi.Calls.Mesas;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class MesasController : ControllerBase{

        private readonly MesasRequests _mesasRequests;


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

            
            if (result is ErrorResponse error)
            {

                return Conflict(error);
            }

            return Ok(result);
        }

    }
}