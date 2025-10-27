using System.ComponentModel.DataAnnotations;
namespace ixCafeApi.Models;

public class EmpregadoCriarRequest
{
    [Required(ErrorMessage = "O Nome é obrigatório.")]
    [StringLength(35, ErrorMessage = "O Nome não pode ter mais de 35 caracteres.")]
    public required string Nome { get; set; }

    [Required(ErrorMessage = "O Pin de Acesso é obrigatório.")]
    public required string PinAcesso { get; set; }

    [Required(ErrorMessage = "O Cargo é obrigatório.")]
    [RegularExpression("^(Empregado|Gerente)$", ErrorMessage = "O Cargo deve ser 'Empregado' ou 'Gerente'.")]
    public required string Cargo { get; set; }

    [Required(ErrorMessage = "O token_role é obrigatório.")]
    [Range(1, int.MaxValue, ErrorMessage = "token_role inválido.")]
    public int TokenRole { get; set; }
}

public class EmpregadoDesativarRequest
{
    [Required(ErrorMessage = "O Nome é obrigatório.")]
    [StringLength(35, ErrorMessage = "O Nome não pode ter mais de 35 caracteres.")]
    public required string Nome { get; set; }

    [Required(ErrorMessage = "O token_role é obrigatório.")]
    [Range(1, int.MaxValue, ErrorMessage = "token_role inválido.")]
    public int TokenRole { get; set; }
}