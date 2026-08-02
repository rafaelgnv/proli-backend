# ProLI API (Backend)

API RESTful desenvolvida em **Java** com **Spring Boot** para gerenciar links, favoritos e pastas de forma hierárquica e segura.

## 🚀 Tecnologias Utilizadas
* **Java 17+**
* **Spring Boot** (Web, Data JPA, Security)
* **MySQL** (Banco de Dados Relacional)
* **JWT (JSON Web Tokens)** para autenticação Stateless
* **Jsoup** para Web Scraping de metadados (OpenGraph)
* **Maven** para gerenciamento de dependências

## ⚙️ Como rodar localmente

1. Clone este repositório.
2. Crie um arquivo `.env` na raiz do projeto baseado no `.env.example`.
3. Preencha as credenciais do seu banco de dados MySQL local no `.env`.
4. Execute a aplicação pela sua IDE (IntelliJ/Eclipse) ou via terminal com:
   ```bash
   ./mvnw spring-boot:run