using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ixCafeApi.Models;


public class PedidoRequest
{
    public int IdEmpregado {get;set;}

    public int IdMesa {get;set;}

    public required List<PedidosList> Pedidos { get; set; }
}

public class PedidosList
{
    public required int IdProduto { get; set; }

    public required int Quantidade { get; set; }

    public string? Observacoes { get; set; }
}


public class ListarPedidosResponse
{
    public int IdEmpregado {get;set;}
    public int IdMesa {get;set;}
    public string PedidoData {get;set;}
    public required List<PedidosList> Pedidos { get; set; }

}