using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using ixCafeApi.Calls.Mesas;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class MesasController : ControllerBase
    {

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




        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpPut]
        [Route("api/mesas/editar")]
        public async Task<IActionResult> Editar(MesasEditarRequest mesasEditarRequest)
        {

            var result = await _mesasRequests.Editar(mesasEditarRequest);


            if (result is ErrorResponse error)
            {

                return NotFound(error);
            }

            return Ok(result);
        }


        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpDelete]
        [Route("api/mesas/eliminar")]
        public async Task<IActionResult> Eliminar(int numeroMesa)
        {

            var result = await _mesasRequests.Eliminar(numeroMesa);


            if (result is ErrorResponse error)
            {

                return NotFound(error);
            }

            return Ok(result);
        }

    }
}