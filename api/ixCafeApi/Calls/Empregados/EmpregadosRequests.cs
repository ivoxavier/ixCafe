using ixCafeApi.Models;

namespace ixCafeApi.Calls.Empregados
{
    public class EmpregadosRequests
    {
        public async Task<object> Criar(EmpregadoCriarRequest empregadoCriarRequest)
        {
            
            await Task.Delay(100); 

            
            return new { Message = "Empregado criado com sucesso!" };
        }
    }
}