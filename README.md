# ProLI - Backend (API) ⚙️

O **ProLI** é um painel de gerenciamento de links, favoritos e pastas. Este repositório contém a API RESTful (Backend), desenvolvida de forma robusta e segura, responsável pelas regras de negócio, persistência de dados, autenticação e web scraping.

> **🔗 Nota:** Este repositório contém apenas a API. Para visualizar e interagir com o sistema, será necessário executar também a interface do usuário.
>
> 👉 **Frontend:** https://github.com/rafaelgnv/proli-frontend

---

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3**
    - Spring Web
    - Spring Data JPA
    - Spring Validation
- **Spring Security** (Autenticação Stateless com JWT)
- **MySQL** (Banco de Dados Relacional)
- **Jsoup** (Web Scraping de metadados OpenGraph)
- **Maven** (Gerenciamento de dependências)

---

## ⚙️ Pré-requisitos

Antes de iniciar, certifique-se de possuir os seguintes softwares instalados:

- [Java JDK 17](https://adoptium.net/) ou superior.
- [MySQL Server](https://dev.mysql.com/downloads/mysql/) em execução na porta **3306**.
- Uma IDE de sua preferência (recomendado: IntelliJ IDEA, Eclipse ou Visual Studio Code).

---

## 🛠️ Como executar o projeto localmente

### 1. Clone o repositório

```bash
git clone https://github.com/rafaelgnv/proli-backend.git
cd proli-backend
```

### 2. Configure as variáveis de ambiente

Certifique-se de que o **MySQL** esteja em execução.

O Hibernate criará automaticamente o banco de dados **`proli_db`**, caso ele ainda não exista.

Em seguida, crie um arquivo chamado **`.env`** na raiz do projeto utilizando o arquivo **`.env.example`** como base.

Preencha o arquivo com as suas credenciais locais:

```env
# Configurações do MySQL
DB_USER=seu_usuario_do_mysql
DB_PASS=sua_senha_do_mysql

# Chave secreta utilizada para geração dos Tokens JWT
JWT_SECRET=uma_chave_secreta_aleatoria_e_muito_longa_aqui_12345

# Credenciais do administrador (criadas automaticamente na primeira execução)
ADMIN_EMAIL=admin@proli.com
ADMIN_PASS=123456
```

### 3. Execute a aplicação

Caso esteja utilizando o terminal, execute:

```bash
./mvnw spring-boot:run
```

Se estiver utilizando o **IntelliJ IDEA**, basta aguardar a sincronização do Maven e clicar no botão **▶ Play** na classe principal `ProliApiApplication.java`.

Após a inicialização, a API estará disponível em:

```text
http://localhost:8080
```

---

## 🔐 Arquitetura de Segurança

A API implementa um sistema de autenticação baseado em **JWT**, utilizando dois tokens para aumentar a segurança da aplicação.

### Access Token

- Token JWT de curta duração (**15 minutos**).
- Retornado no corpo da resposta de autenticação.
- Deve ser enviado no cabeçalho:

```http
Authorization: Bearer <access_token>
```

### Refresh Token

- Token JWT de longa duração (**7 dias**).
- Enviado automaticamente como um **cookie HttpOnly**.
- Não é acessível via JavaScript, reduzindo significativamente o risco de ataques **XSS (Cross-Site Scripting)**.

### Isolamento de Dados

A aplicação implementa isolamento completo dos dados entre usuários.

Filtros e validações em nível de serviço e persistência garantem que cada usuário possa acessar apenas suas próprias pastas, links e favoritos, sempre vinculados ao seu respectivo **`user_id`**.

---

## 📌 Observações

- O banco de dados é criado automaticamente na primeira execução.
- O usuário administrador é criado automaticamente utilizando as credenciais definidas no arquivo `.env`.
- Para utilizar o sistema completo, execute também o projeto **Frontend** disponível em:

👉 https://github.com/rafaelgnv/proli-frontend