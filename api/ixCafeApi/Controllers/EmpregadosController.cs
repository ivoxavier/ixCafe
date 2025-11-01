using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using ixCafeApi.Calls.Empregados;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class EmpregadosController : ControllerBase{

        private readonly EmpregadosRequests _empregadosRequests;


        public EmpregadosController(EmpregadosRequests empregadosRequests)
        {
            _empregadosRequests = empregadosRequests;
        }


        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        [Authorize(Roles = "Gerente,Admin")]
        [HttpPost]
        [Route("api/empregados/criar")]
        public async Task<IActionResult> Criar(EmpregadoCriarRequest empregadoCriarRequest)
        {

            var result = await _empregadosRequests.Criar(empregadoCriarRequest);


            if (result is ErrorResponse error)
            {

                return Conflict(error);
            }

            return Ok(result);
        }




        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(typeof(ErrorResponse), StatusCodes.Status409Conflict)]
        [Authorize(Roles = "Gerente,Admin")]
        [HttpPut]
        [Route("api/empregados/desativar")]
        public async Task<IActionResult> Desativar(EmpregadoDesativarRequest empregadoDesativarRequest)
        {

            var result = await _empregadosRequests.Desativar(empregadoDesativarRequest);

            if (result is ErrorResponse error)
            {

                return NotFound();
            }

            return Ok();
        }
        



        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpDelete]
        [Route("api/empregados/eliminar")]
        public async Task<IActionResult> Eliminar(string nomeEmpregado, string cargo)
        {

            var result = await _empregadosRequests.Eliminar(nomeEmpregado,cargo);


            if (result is ErrorResponse error)
            {

                return NotFound(error);
            }

            return Ok(result);
        }




        [ProducesResponseType(typeof(ListaEmpregadosResponse), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [HttpGet]
        [Route("api/empregados/listar")]
        public async Task<IActionResult> Listar ()
        {
            var result = await _empregadosRequests.Listar();

            if(result is ErrorResponse error)
            {
                return NotFound(error);
            }


            return Ok(result);


        }





    }
}