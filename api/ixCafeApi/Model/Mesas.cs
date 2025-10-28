using System.ComponentModel.DataAnnotations;
namespace ixCafeApi.Models;

public class MesasCriarRequest
{   
    [Required(ErrorMessage = "O Número da Mesa é obrigatório.")]
    public required int NumeroMesa { get; set; }

    [Required(ErrorMessage = "A Localização da Mesa é obrigatória.")]
    [StringLength(50, ErrorMessage = "A Localização não pode ter mais de 50 caracteres.")]
    public required string Localizacao { get; set; }

    [Required(ErrorMessage = "A Capacidade é obrigatória.")]
    public required int Capacidade { get; set; }

}