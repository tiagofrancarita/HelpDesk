
# Helpdesk System


O Helpdesk System é uma aplicação completa de gerenciamento de chamados e tickets de suporte, construída com Java 17, Spring Boot e MySQL. O sistema permite a gestão de tickets de suporte, categorização de problemas, acompanhamento de status e muito mais, proporcionando uma ferramenta robusta para equipes de TI e suporte.


## Funcionalidades

- Gestão de Usuários: Cadastro e autenticação de usuários (clientes e técnicos).
- Criação e Gestão de Tickets: Permite a criação, atualização e fechamento de tickets de suporte.
- Categorias de Problemas: Organização dos tickets por categorias para facilitar o gerenciamento.
- Notificações: Envio de notificações por e-mail para atualizações de status dos tickets.
- Relatórios: Geração de relatórios de desempenho e status dos tickets.


## Requisitos

- **Java 17**:        [Download Java](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- **Maven 3.6+**:     [Download Maven](https://maven.apache.org/download.cgi)
- **MySQL 8.0**:      [Download MySQL](https://dev.mysql.com/downloads/mysql/)
- **Postman 11.1.0**: [Download Postman](https://www.postman.com/downloads/)


## Instalação

### Clonando o Repositório

```bash
git clone https://github.com/tiagofrancarita/HelpDesk.git
cd helpdesk
```

# Configurando o Banco de dados.

### 1. Crie um banco de dados.
```sql
 CREATE DATABASE helpdesk_db;
```

### 2. Configure as credenciais do banco de dados no arquivo application.properties:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/helpdesk_db
spring.datasource.username=seu-usuario
spring.datasource.password=sua-senha
spring.jpa.hibernate.ddl-auto=update
```

# Compilando e executando a aplicação.
```bash
./mvnw clean install
./mvnw spring-boot:run
```
A aplicação estará disponível em http://localhost:8080.

## Stack utilizada

**Front-end:** Angular 16+

**Back-end:** Java Springboot 17

**Banco de dados:** Mysql 8.0




# Documentação da API

## Endpoints Principais

# Usuários
- POST /api/users/cadastrarUsuario: Cadastro de novo usuário.
- POST /api/users/login: Autenticação de usuário.
- GET /api/users/listarUsuario: Listagem de todos os usuários cadastrados.
- GET /api/users/buscarPorId/{id}: busca usuário por id.
- DELETE /api/users/deletarUsuarioPorId/{id}: deletar usuário por id.

```http
  POST /api/users/cadastrarUsuario
```
|Parâmetro  	| Tipo 	| Descrição|
|---	|---	|---	|
| `id` 	| `Long`  	| **Obrigatório**. Chave unica |
| `nome` 	| `string`  	| **Campo obrigatório.**|
| `email` 	| `string`  	| **Campo obrigatório.** |
| `dataCadastro` 	| `Date`  	| **Campo obrigatório.** |
| `status` 	| `string`  	| **Campo obrigatório.** |
| `cpf` 	| `string`  	| **Campo obrigatório.** |
| `senha` 	| `string`  	| **Campo obrigatório.** |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

#### Realiza o login
Re
  ```http
  POST /api/users/login
```
Recebe os parametros  `email` `senha` para realizar o login e gerar a `api_key`

|Parâmetro  	| Tipo 	| Descrição|
|---	|---	|---	|
| `email` 	| `string`  	| ----- |
| `senha` 	| `string`  	| -------|


#### Retorna todos os usuarios cadastrados
 ```http
  GET /api/users/listarUsuario
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

#### Retorna um usuario
Recebe um parametro `id` para realizar a busca do usuário associado aquele id
```http
  GET /api/users/buscarPorId/{id}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`        | `string`   | **Obrigatório**. O ID do usuario que você quer |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

```http
DELETE /api/users/buscarPorId/{id}
```
Recebe um parametro `id` para realizar a exclusão do usuário associado aquele id

| Parâmetro   | Tipo       | Descrição|
| :---------- | :--------- | :----------|
| `id`        | `string`   | **Obrigatório**. O ID do usuario que você quer |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

# Tickets / Chamados
- POST /api/tickets/cadastrarTicket: Cadastro de novo tickets.
- GET /api/tickets/listarTicket: Listagem de todos os tickets cadastrados.
- GET /api/tickets/buscarPorId/{id}: busca tickets por id.
- DELETE /api/tickets/deletarTicketPorId/{id}: deletar tickets por id.
- PUT /api/tickets/atualizaTicket/{id}: Atualiza tickets por id.

```http
POST /api/tickets/cadastrarTicket
```
Cadastra um ticket com base nos parametros abaixo preenchidos

|Parâmetro  	| Tipo 	| Descrição|
|---	|---	|---	|
| `id` 	| `Long`  	| **Obrigatório**. Chave unica |
| `idTecnico` 	| `Long`  	| **Campo obrigatório.**|
| `descriçãoChamado` 	| `string`  	| **Campo obrigatório.** |
| `dataCadastro` 	| `Date`  	| **Campo obrigatório.** |
| `status` 	| `string`  	| **Campo obrigatório.** |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |


#### Retorna todos os tickets cadastrados
 ```http
  GET /api/tickets/listarTicket
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |


#### Retorna um ticket
Recebe um parametro `id` para realizar a busca do ticket associado aquele id
```http
  GET /api/tickets/buscarPorId/{id}
```
| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`        | `string`   | **Obrigatório**. O ID do usuario que você quer |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

```http
DELETE /api/users/deletarTicketPorId/{id}
```
Recebe um parametro `id` para realizar a exclusão do ticket associado aquele id

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`        | `string`   | **Obrigatório**. O ID do ticket que você quer |
| `api_key`   | `string`   | **Obrigatório**. A chave da sua API |

#### Atualiza um ticket
Recebe um parametro `id` para realizar a busca do ticket para realizar a atualização
```http
  GET /api/tickets/atualizaTicket/{id}
```
| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `id`        | `string`   | **Obrigatório**. O ID do ticket que você quer |
| `api_key`   | `string`   | **Obrigatório**. A ch





## Estrutura do Projeto

```plaintext
src
├── main
│   ├── java
│   │   └── com
│   │       └── helpdesk
│   │           ├── controller    # Controladores REST
│   │           ├── model         # Modelos de dados
│   │           ├── repository    # Repositórios JPA
│   │           ├── service       # Serviços de negócios
│   │           └── HelpdeskApplication.java  # Classe principal da aplicação
│   └── resources
│       ├── application.properties  # Configurações da aplicação
│       ├── schema.sql              # Script de criação do esquema do banco de dados (opcional)
│       ├── application.properties  # Configurações da aplicação
│       
└── test
    └── java
        └── com
            └── helpdesk          # Testes unitários e de integração
```
## Licença
Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

[MIT](https://choosealicense.com/licenses/mit/)


## Suporte

### Contato

Para dúvidas ou sugestões, entre em contato através do email: tiagofranca.rita@gmail.com

Feito com ❤️ por Tiago.


## Etiquetas

Adicione etiquetas de algum lugar, como: [shields.io](https://shields.io/)

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
[![AGPL License](https://img.shields.io/badge/license-AGPL-blue.svg)](http://www.gnu.org/licenses/agpl-3.0)

