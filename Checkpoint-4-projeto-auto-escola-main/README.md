# AutoEscola3ESPH

API REST desenvolvida em **Java com Spring Boot** para gerenciamento de uma autoescola, incluindo cadastro de alunos, instrutores e usuários, autenticação com JWT, agendamento e cancelamento de instruções.

Projeto desenvolvido para o **Checkpoint 5 da disciplina SOA e WebServices — FIAP**.

## 👥 Integrantes

Paulo Poças - RM556080
André Luiz Fernandes de Queiroz - RM554503
Rafael Bocchi - RM557603
Rafael Federici de Oliveira - RM554736
Marcos Vinícius da Silva Costa - RM555490

## 🎯 Objetivo

O projeto tem como objetivo desenvolver uma API REST para uma autoescola, permitindo o gerenciamento de usuários, alunos, instrutores e instruções de direção, utilizando autenticação e autorização para proteger os recursos da aplicação.

Nesta etapa do projeto foram implementados os requisitos do Checkpoint 4, incluindo cadastro de alunos e usuários, criptografia de senhas, controle de acesso por perfil, alteração de senha e cancelamento de instruções.

## 🛠️ Tecnologias utilizadas

* Java 25
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* BCrypt
* MySQL
* Flyway
* Lombok
* Maven
* Springdoc OpenAPI (Swagger)
* JUnit 5 e Mockito
* API externa ViaCEP

## 🔐 Autenticação e autorização

A API utiliza **JWT (JSON Web Token)** para autenticação.

Para acessar os endpoints protegidos, o usuário deve realizar login e utilizar o token retornado nas requisições através do header:

```text
Authorization: Bearer SEU_TOKEN
```

O sistema possui controle de acesso baseado no perfil do usuário.

Usuários com perfil **ADMIN** possuem acesso às operações de gerenciamento de usuários, enquanto usuários autenticados podem realizar operações permitidas ao seu perfil.

As senhas dos usuários não são armazenadas em texto puro. Elas são criptografadas utilizando **BCrypt** antes de serem armazenadas no banco de dados.

## 📌 Principais funcionalidades

### Alunos

* CRUD completo de alunos (exclusão lógica)
* Armazenamento dos dados pessoais e endereço
* Associação do aluno às instruções agendadas

### Instrutores

* Cadastro de instrutores
* Informações de contato
* CNH
* Especialidade
* Endereço
* Controle de instrutores ativos e inativos

### Usuários

* Cadastro de usuários
* Autenticação utilizando JWT
* Criptografia de senha com BCrypt
* Listagem de usuários
* Atualização do perfil
* Exclusão de usuários
* Alteração da própria senha

As operações administrativas de gerenciamento de usuários são restritas a usuários com perfil **ADMIN**.

### Agendamento de instruções

A API permite o agendamento de instruções entre alunos e instrutores.

São consideradas regras de negócio como:

* Horário de funcionamento da autoescola;
* Funcionamento de segunda-feira a sábado;
* Horário entre 06:00 e 21:00;
* Antecedência mínima para o agendamento;
* Verificação da existência do aluno e instrutor;
* Verificação de disponibilidade do instrutor;
* Especialidade do instrutor quando necessário.

### Cancelamento de instruções

Também foi implementado o cancelamento de instruções.

O cancelamento exige a informação de um motivo:

* `ALUNO_DESISTIU`
* `INSTRUTOR_CANCELOU`
* `OUTROS`

Uma instrução só pode ser cancelada quando houver **pelo menos 24 horas de antecedência**.

Após o cancelamento, a instrução passa a possuir o status:

```text
CANCELADA
```

e o motivo informado fica registrado na base de dados.

## 📡 Principais endpoints

### Autenticação

| Método | Endpoint | Descrição                                |
| ------ | -------- | ---------------------------------------- |
| POST   | `/login` | Autenticação do usuário e geração do JWT |

### Alunos

| Método | Endpoint       | Descrição                         |
| ------ | -------------- | --------------------------------- |
| POST   | `/alunos`      | Cadastro de aluno                 |
| GET    | `/alunos`      | Lista os alunos ativos (paginado) |
| GET    | `/alunos/{id}` | Detalha um aluno                  |
| PUT    | `/alunos`      | Atualiza os dados do aluno        |
| DELETE | `/alunos/{id}` | Exclusão lógica do aluno          |

### Usuários

| Método | Endpoint                | Descrição                  |
| ------ | ----------------------- | -------------------------- |
| POST   | `/usuarios`             | Cadastro de usuário        |
| GET    | `/usuarios`             | Listagem de usuários       |
| PUT    | `/usuarios/{id}`        | Atualização do perfil      |
| DELETE | `/usuarios/{id}`        | Exclusão de usuário        |
| PUT    | `/usuarios/minha-senha` | Alteração da própria senha |

As operações administrativas de usuários exigem autenticação com perfil **ADMIN**.

### Instrutores

Os endpoints de instrutores permitem o gerenciamento dos instrutores da autoescola, incluindo cadastro, consulta, atualização e exclusão lógica.

### Instruções

| Método | Endpoint                    | Descrição                 |
| ------ | --------------------------- | ------------------------- |
| POST   | `/instrucoes`               | Agendamento de instrução  |
| PUT    | `/instrucoes/{id}/cancelar` | Cancelamento de instrução |

### Endereços (API externa)

| Método | Endpoint               | Descrição                                   |
| ------ | ---------------------- | ------------------------------------------- |
| GET    | `/enderecos/cep/{cep}` | Consulta endereço pelo CEP usando o ViaCEP  |

## 🗄️ Banco de dados

A aplicação utiliza **MySQL** como banco de dados.

As alterações estruturais do banco são controladas através do **Flyway**, permitindo que as migrations sejam executadas de forma organizada durante a evolução do projeto.

Entre as principais entidades estão:

* `usuarios`
* `alunos`
* `instrutores`
* `instrucoes`

## ▶️ Como executar o projeto

### Pré-requisitos

* Java 25 ou superior
* Maven
* MySQL

### Configuração

Configure as informações de conexão com o banco de dados no arquivo de propriedades da aplicação:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/NOME_DO_BANCO
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

Depois, execute o projeto utilizando Maven:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A aplicação será iniciada conforme a porta configurada no projeto.

## 🔄 Migrations

O projeto utiliza Flyway para versionamento do banco de dados.

As migrations são executadas automaticamente durante a inicialização da aplicação, criando e alterando as tabelas necessárias para o funcionamento da API.

## 📚 Checkpoint 5

### Consumo de API externa (ViaCEP)

O endpoint `GET /enderecos/cep/{cep}` consome a API pública [ViaCEP](https://viacep.com.br) usando o `RestClient` do Spring e devolve o endereço no mesmo formato do `DadosEndereco`, facilitando o cadastro de alunos e instrutores.

Exemplo: `GET /enderecos/cep/01310100`

Tratamento de erros: CEP inválido retorna 400, CEP inexistente retorna 404 e ViaCEP fora do ar retorna 503.

### Documentação com Swagger

Com a aplicação rodando, acesse:

* Swagger UI: `http://localhost:8085/swagger-ui.html`
* OpenAPI (JSON): `http://localhost:8085/v3/api-docs`

Para testar endpoints protegidos, faça login em `/login`, copie o token, clique em **Authorize** e cole o token JWT.

### CORS

O CORS está configurado no `SecurityConfig`. Por padrão ficam liberadas as origens `http://localhost:3000` e `http://localhost:5173`, com os métodos GET, POST, PUT, DELETE e OPTIONS e os headers `Authorization` e `Content-Type`. Para liberar outra origem, altere a lista em `corsConfigurationSource()`.

### Testes automatizados

Testes unitários com JUnit 5 e Mockito, um para cada entidade:

* `InstrutorServiceTest`
* `AlunoServiceTest`
* `UsuarioServiceTest`
* `AgendaDeInstrucoesTest` (instruções)
* `ViaCepServiceTest`

Para executar:

```bash
./mvnw test
```

O teste `AutoEscola3EsphApplicationTests` sobe o contexto completo e por isso exige o MySQL em execução.

## 📚 Checkpoint 4

Este projeto contempla os requisitos solicitados no Checkpoint 4 da disciplina **SOA e WebServices**, incluindo:

* Cadastro de alunos;
* Cadastro de usuários;
* Criptografia de senhas;
* Autenticação utilizando JWT;
* Autorização de operações administrativas;
* Alteração da própria senha;
* Cancelamento de instruções;
* Aplicação das regras de negócio relacionadas ao cancelamento.

## 📄 Licença

Projeto acadêmico desenvolvido para a FIAP.
