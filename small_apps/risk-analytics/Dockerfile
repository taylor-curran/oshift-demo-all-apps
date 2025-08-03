# Minimal Dockerfile for risk-analytics (.NET Core)
FROM mcr.microsoft.com/dotnet/sdk:6.0 AS builder

WORKDIR /app
COPY *.csproj ./
RUN dotnet restore

COPY . ./
RUN dotnet publish -c Release -o out

FROM mcr.microsoft.com/dotnet/aspnet:6.0
WORKDIR /app

COPY --from=builder /app/out .

EXPOSE 8080
ENV ASPNETCORE_URLS=http://+:8080
CMD ["dotnet", "RiskAnalytics.dll"]