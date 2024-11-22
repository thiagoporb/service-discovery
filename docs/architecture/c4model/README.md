# Documentação C4 Model - Service Discovery

## Nível 1: Diagrama de Contexto

```plantuml
@startuml C4_Context
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml

Person(client, "Cliente", "Usuário do sistema")
System(serviceDiscovery, "Service Discovery", "Sistema de registro e descoberta de serviços")
System_Ext(microservices, "Microsserviços", "Serviços que se registram no Service Discovery")

Rel(client, serviceDiscovery, "Consulta serviços disponíveis")
Rel(microservices, serviceDiscovery, "Registra-se e consulta outros serviços")

@enduml
```

## Nível 2: Diagrama de Contêiner

```plantuml
@startuml C4_Container
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

Person(client, "Cliente", "Usuário do sistema")
System_Boundary(serviceDiscovery, "Service Discovery") {
    Container(eurekaServer, "Eureka Server", "Spring Cloud Netflix", "Servidor de registro e descoberta")
    Container(actuator, "Spring Actuator", "Spring Boot", "Monitoramento e métricas")
    ContainerDb(registry, "Registro de Serviços", "Em memória", "Armazena informações dos serviços registrados")
}

System_Ext(microservices, "Microsserviços", "Serviços que se registram no Service Discovery")

Rel(client, eurekaServer, "Consulta", "HTTP/REST")
Rel(microservices, eurekaServer, "Registra-se/Consulta", "HTTP/REST")
Rel(eurekaServer, registry, "Lê/Escreve")
Rel(actuator, eurekaServer, "Monitora")

@enduml
```

## Nível 3: Diagrama de Componentes

```plantuml
@startuml C4_Components
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml

Container_Boundary(serviceDiscovery, "Service Discovery") {
    Component(registrationController, "Registration Controller", "Spring REST", "Gerencia registro de serviços")
    Component(discoveryController, "Discovery Controller", "Spring REST", "Gerencia descoberta de serviços")
    Component(registryService, "Registry Service", "Spring Service", "Lógica de negócio do registro")
    Component(healthCheck, "Health Check", "Spring Actuator", "Monitora saúde dos serviços")
    Component(metrics, "Metrics", "Spring Actuator", "Coleta métricas")
    
    ContainerDb(registry, "Registro", "Em memória", "Armazena dados dos serviços")
}

Rel(registrationController, registryService, "Usa")
Rel(discoveryController, registryService, "Usa")
Rel(registryService, registry, "Lê/Escreve")
Rel(healthCheck, registryService, "Monitora")
Rel(metrics, registryService, "Coleta")

@enduml
```

## Descrição dos Componentes

### Contexto
- **Service Discovery**: Sistema central responsável pelo registro e descoberta de serviços
- **Microsserviços**: Aplicações externas que se registram no Service Discovery
- **Cliente**: Usuários ou sistemas que consultam os serviços disponíveis

### Contêineres
1. **Eureka Server**
   - Implementação principal do servidor de registro e descoberta
   - Baseado no Spring Cloud Netflix
   - Expõe endpoints REST para registro e consulta

2. **Spring Actuator**
   - Fornece monitoramento e métricas
   - Endpoints para health check
   - Informações operacionais do sistema

3. **Registro de Serviços**
   - Armazenamento em memória
   - Mantém informações dos serviços registrados
   - Cache de dados e estado dos serviços

### Componentes
1. **Registration Controller**
   - Gerencia requisições de registro
   - Valida dados de entrada
   - Processa registros de novos serviços

2. **Discovery Controller**
   - Gerencia requisições de descoberta
   - Retorna informações dos serviços
   - Implementa lógica de balanceamento

3. **Registry Service**
   - Lógica de negócio central
   - Gerencia ciclo de vida dos registros
   - Implementa regras de negócio

4. **Health Check**
   - Monitora saúde dos serviços
   - Verifica disponibilidade
   - Atualiza status dos serviços

5. **Metrics**
   - Coleta métricas operacionais
   - Monitora performance
   - Gera dados para análise

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Cloud Netflix Eureka**
- **Spring Actuator**
- **Maven**
- **Docker/Jib**

## Portas e Endpoints

- **Porta**: 8761
- **Endpoints principais**:
  - `/eureka/apps` (GET) - Lista serviços registrados
  - `/eureka/apps/{appId}` (GET) - Detalhes de um serviço
  - `/actuator/health` (GET) - Status da saúde
  - `/actuator/metrics` (GET) - Métricas do sistema

# Estrutura de Diretórios Recomendada

```
service-discovery/
├── docs/
│   ├── architecture/
│   │   ├── c4model/
│   │   │   ├── diagrams/
│   │   │   │   ├── context.puml
│   │   │   │   ├── container.puml
│   │   │   │   └── component.puml
│   │   │   ├── images/
│   │   │   │   ├── context.png
│   │   │   │   ├── container.png
│   │   │   │   └── component.png
│   │   │   └── README.md
│   │   └── decisions/
│   │       └── adr/
│   └── README.md
```

## Conteúdo dos Arquivos

### `docs/architecture/c4model/README.md`:

```markdown
# Documentação Arquitetural - Service Discovery

## Visão Geral
Este documento contém a documentação arquitetural do Service Discovery utilizando o modelo C4.

## Estrutura da Documentação
- [Diagrama de Contexto](diagrams/context.puml)
- [Diagrama de Container](diagrams/container.puml)
- [Diagrama de Componentes](diagrams/component.puml)

## Como Gerar os Diagramas
1. Instale o PlantUML (https://plantuml.com/starting)
2. Execute o comando:
   ```bash
   plantuml diagrams/*.puml -o ../images
   ```

## Tecnologias Utilizadas
- Java 17
- Spring Boot 3.3.4
- Spring Cloud Netflix Eureka
- Docker/Jib

## Componentes Principais
[Descrição detalhada dos componentes...]
```

### `docs/architecture/decisions/adr/0001-uso-eureka-server.md`:

```markdown
# 1. Uso do Eureka Server como Service Discovery

Data: 2024-03-XX

## Status
Aceito

## Contexto
Necessidade de um sistema de registro e descoberta de serviços para nossa arquitetura de microsserviços.

## Decisão
Utilizar Netflix Eureka Server devido à:
- Integração nativa com Spring Cloud
- Maturidade da solução
- Ampla adoção pela comunidade

## Consequências
### Positivas
- Fácil integração com ecossistema Spring
- Documentação abundante
- Comunidade ativa

### Negativas
- Overhead de memória
- Complexidade adicional na infraestrutura
```

### `.gitignore`:

```
# Ignorar imagens geradas dos diagramas
docs/architecture/c4model/images/*.png
```

### `docs/README.md`:

```markdown
# Service Discovery - Documentação

## Índice
1. [Arquitetura](architecture/c4model/README.md)
   - [Diagramas C4 Model](architecture/c4model/diagrams/)
   - [Decisões Arquiteturais](architecture/decisions/adr/)
2. [API](api/README.md)
3. [Deployment](deployment/README.md)

## Links Úteis
- [Repositório GitHub](https://github.com/seu-usuario/service-discovery)
- [Wiki do Projeto](https://github.com/seu-usuario/service-discovery/wiki)
```

### GitHub Actions Workflow:

```yaml
name: Generate Architecture Diagrams

on:
  push:
    paths:
      - 'docs/architecture/c4model/diagrams/**'

jobs:
  generate-diagrams:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Generate PlantUML Diagrams
        uses: cloudbees/plantuml-github-action@v1.0.0
        with:
          args: -v -tpng docs/architecture/c4model/diagrams/*.puml -o docs/architecture/c4model/images/
```