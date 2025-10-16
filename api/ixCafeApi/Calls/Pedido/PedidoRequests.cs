using ixCafeApi.Models;

namespace ixCafeApi.Calls.Mesas
{
    public class PedidoRequests
    {
        public async Task<object> Pedido(PedidoRequest pedidoRequest)
        {
            
            await Task.Delay(100); 

            
            return new { Message = "Pedido criado com sucesso!" };
        }
    }
}