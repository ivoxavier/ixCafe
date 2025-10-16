using ixCafeApi.Calls.Empregados;
using ixCafeApi.Calls.Produtos;
using ixCafeApi.Calls.Mesas;
var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddControllers();

builder.Services.AddScoped<EmpregadosRequests>();
builder.Services.AddScoped<ProdutosRequests>();
builder.Services.AddScoped<MesasRequests>();
builder.Services.AddScoped<PedidoRequests>();

builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();


app.MapControllers();

app.Run();


