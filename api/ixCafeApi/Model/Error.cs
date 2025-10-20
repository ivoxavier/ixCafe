using System.ComponentModel.DataAnnotations;
namespace ixCafeApi.Models;

public class ErrorResponse
{
    public required int ErrorCode {get;set;}

    public string ErrorMessage {get;set;}

}