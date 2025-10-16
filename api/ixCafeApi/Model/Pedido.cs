using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ixCafeApi.Models;


public class PedidoRequest
{
    public int IdEmpregado {get;set;}

    public int IdMesa {get;set;}

    public string DataHora {get;set;}

    public List<PedidosList> Pedidos { get; set; }
}

public class PedidosList
{
    public int IdProduto { get; set; }

    public int Quantidade { get; set; }

    public string Observacoes { get; set; }
}