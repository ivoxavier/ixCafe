using Dapper; 
using MySqlConnector;
using ixCafeApi.Services; 
using System.Data;

namespace ixCafeApi.Services
{
    public class DatabaseTokenValidationService : ITokenValidationService
    {
        private readonly string _connectionString;

        
        public DatabaseTokenValidationService(IConfiguration configuration)
        {
           
            _connectionString = configuration.GetConnectionString("DefaultConnection") 
                ?? throw new InvalidOperationException("Connection string 'DefaultConnection' não encontrada no appsettings.json");
        }

        public async Task<List<string>?> ValidateTokenAsync(string token)
        {
            var roles = new List<string>();

            
            await using (var connection = new MySqlConnection(_connectionString))
            {
                var parameters = new { p_token_value = token };

                
                var result = await connection.QueryAsync<string>(
                    "sp_ValidateTokenAndGetRoles", 
                    parameters, 
                    commandType: CommandType.StoredProcedure 
                );

                roles = result.ToList();
            }

            
            if (roles.Count == 0)
            {
                return null; 
            }

            return roles;
        }
    }
}