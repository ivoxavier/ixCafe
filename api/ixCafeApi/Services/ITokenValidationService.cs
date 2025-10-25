namespace ixCafeApi.Services
{
    public interface ITokenValidationService
    {
        /// <summary>
        /// Valida um token estático e retorna as roles associadas.
        /// </summary>
        /// <param name="token">O token (sem o "Bearer ").</param>
        /// <returns>Uma lista de roles se o token for válido; null ou lista vazia se for inválido.</returns>
        Task<List<string>?> ValidateTokenAsync(string token);
    }
}