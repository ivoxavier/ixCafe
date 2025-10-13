using ixCafeApi.Models;

namespace ixCafeApi.Calls.Mesas
{
    public class MesasRequests
    {
        public async Task<object> Criar(MesasCriarRequest mesasCriarRequest)
        {
            
            await Task.Delay(100); 

            
            return new { Message = "Mesa criado com sucesso!" };
        }
    }
}