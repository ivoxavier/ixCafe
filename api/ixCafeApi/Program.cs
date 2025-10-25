using ixCafeApi.Calls.Empregados;
using ixCafeApi.Calls.Produtos;
using ixCafeApi.Calls.Mesas;
using ixCafeApi.Services;
using ixCafeApi.Authentication;
using Microsoft.AspNetCore.Authentication;
using Microsoft.OpenApi.Models;
var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddControllers();

builder.Services.AddScoped<EmpregadosRequests>();
builder.Services.AddScoped<ProdutosRequests>();
builder.Services.AddScoped<MesasRequests>();
builder.Services.AddScoped<PedidoRequests>();

builder.Services.AddScoped<ITokenValidationService, DatabaseTokenValidationService>();

builder.Services.AddAuthentication("Bearer") 
    .AddScheme<AuthenticationSchemeOptions, StaticBearerAuthenticationHandler>("Bearer", null);

builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "ixCafe API", Version = "v1" });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http, 
        Scheme = "Bearer",
        BearerFormat = "Token",
        In = ParameterLocation.Header,
        Description = "Insira o seu token Bearer (Ex: 'Bearer 9f38c7a1b24e...')"
    });
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" }
            },
            new string[] {}
        }
    });
});




builder.Services.AddAuthorization();



var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();


app.MapControllers();

app.Run();


