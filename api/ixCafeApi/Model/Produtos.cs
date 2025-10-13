using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
namespace ixCafeApi.Models;

public class ProdutosCriarRequest
{
    [Required(ErrorMessage = "O ID da Categoria é obrigatório.")]
    public required int IdCategoria { get; set; }

    [StringLength(50, ErrorMessage = "O Nome não pode ter mais de 50 caracteres.")]
    public required string Nome { get; set; }

    public string? Descricao { get; set; }

    [Required(ErrorMessage = "O Preço é obrigatório.")]
    [Column(TypeName = "decimal(10, 2)")]
    [Range(0.01, 99999999.99, ErrorMessage = "O preço deve ser maior que zero.")] 
    public required decimal Preco {get;set;}
}