using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Options;
using System.Security.Claims;
using System.Text.Encodings.Web;
using System.Net.Http.Headers;
using ixCafeApi.Services;

namespace ixCafeApi.Authentication
{
    public class StaticBearerAuthenticationHandler : AuthenticationHandler<AuthenticationSchemeOptions>
    {
        private readonly ITokenValidationService _tokenService;

        public StaticBearerAuthenticationHandler(
            IOptionsMonitor<AuthenticationSchemeOptions> options,
            ILoggerFactory logger,
            UrlEncoder encoder,
            ISystemClock clock,
            ITokenValidationService tokenService)
            : base(options, logger, encoder, clock)
        {
            _tokenService = tokenService;
        }

        protected override async Task<AuthenticateResult> HandleAuthenticateAsync()
        {
            
            if (!Request.Headers.ContainsKey("Authorization"))
            {
                return AuthenticateResult.NoResult();
            }

            AuthenticationHeaderValue authHeader;
            try
            {
                authHeader = AuthenticationHeaderValue.Parse(Request.Headers["Authorization"]);
            }
            catch
            {
                return AuthenticateResult.Fail("Header de Autorização mal formatado.");
            }

            
            if (!"Bearer".Equals(authHeader.Scheme, StringComparison.OrdinalIgnoreCase))
            {
                return AuthenticateResult.Fail("Scheme de Autorização inválido. Esperado 'Bearer'.");
            }

            var token = authHeader.Parameter;
            if (string.IsNullOrEmpty(token))
            {
                return AuthenticateResult.Fail("Token não fornecido.");
            }

           
            var roles = await _tokenService.ValidateTokenAsync(token);

            if (roles == null)
            {
                return AuthenticateResult.Fail("Token inválido.");
            }

           
            var claims = new List<Claim>
            {
                
                new Claim(ClaimTypes.NameIdentifier, $"user_token_{token.Substring(0, 5)}")
            };

            
            foreach (var role in roles)
            {
                claims.Add(new Claim(ClaimTypes.Role, role));
            }

            var identity = new ClaimsIdentity(claims, Scheme.Name);
            var principal = new ClaimsPrincipal(identity);
            var ticket = new AuthenticationTicket(principal, Scheme.Name);

            
            return AuthenticateResult.Success(ticket);
        }
    }
}