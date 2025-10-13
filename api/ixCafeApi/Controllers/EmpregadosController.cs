using Microsoft.AspNetCore.Mvc;
using ixCafeApi.Calls.Empregados;
using ixCafeApi.Models;

namespace ixCafeApi.Controllers{

    [ApiController]
    public class EmpregadosController : ControllerBase{

        private readonly EmpregadosRequests _empregadosRequests = new EmpregadosRequests();


        public EmpregadosController(EmpregadosRequests empregadosRequests)
        {
            _empregadosRequests = empregadosRequests;
        }

        [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [HttpPost]
        [Route("api/empregados/criar")]
        public async Task<IActionResult> Criar(EmpregadoCriarRequest empregadoCriarRequest)
        {
        
            var result = await _empregadosRequests.Criar(empregadoCriarRequest); 

            
            return Ok(result);
        }

    }
}