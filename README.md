# Autorizador de transações

API REST demonstrativa para cadastro e consulta de cartões e autorização de transações. O sistema mantém cartões e transações em banco relacional e valida as regras da compra antes de debitar o saldo.

## Tecnologias utilizadas

- **Java 21**: versão da linguagem definida no projeto, com suporte a recursos atuais da plataforma.
- **Spring Boot 4.1.1**: inicialização e configuração da aplicação, além da integração dos componentes Spring.
- **Spring Web MVC**: criação dos endpoints REST e respostas HTTP.
- **Spring Data JPA e Hibernate**: persistência das entidades e acesso aos repositórios sem implementar manualmente operações CRUD. Cada transação tem um cartão obrigatório associado; um cartão pode ter várias transações. A busca do cartão por número usa bloqueio pessimista para serializar transações concorrentes sobre o mesmo cartão.
- **MySQL 5.7**: banco usado pela aplicação local, conforme o serviço `mysql` do `docker-compose.yml`.
- **Jakarta Bean Validation**: validação dos dados recebidos nos DTOs, como número e senha do cartão e valor positivo da transação.
- **Spring Security**: proteção dos endpoints com autenticação HTTP Basic.
- **Lombok**: redução de código repetitivo, como getters e setters das entidades.
- **JUnit 5 e Mockito**: testes unitários dos controllers e serviços, com dependências de teste do Spring Boot.
- **Maven**: gerenciamento de dependências e build, com Maven Wrapper incluído no repositório.

## Organização do código

```text
src/main/java/br/com/vr/autorizador/
├── config/        Configuração de segurança
├── controller/    Endpoints HTTP
├── dtos/          Contratos de entrada da API e validações
├── models/        Entidades JPA Cartao (um para muitos) e Transacao (muitos para um)
├── repositories/  Repositórios Spring Data
└── service/       Regras de negócio e operações de persistência

src/test/java/     Testes de contexto, controllers e serviços
```

Os controllers traduzem requisições para respostas HTTP; os serviços executam as regras de negócio; os repositórios acessam o banco. DTOs evitam usar diretamente as entidades como contrato de entrada da API.

## Funcionalidades e endpoints

Todos os endpoints exigem autenticação HTTP Basic.

### Cartões

| Método | Caminho | Comportamento implementado |
|---|---|---|
| `POST` | `/cartoes` | Recebe `numeroCartao` e `senha`, cria o cartão com saldo inicial de 500 e retorna o DTO com HTTP 201. |
| `GET` | `/cartoes/{numeroCartcao}` | Consulta o cartão pelo número e retorna o saldo com HTTP 200 ou HTTP 404 se não existir. |

Exemplo de corpo para cadastro:

```json
{
  "numeroCartao": "1234567890123456",
  "senha": "1234"
}
```

### Transações

| Método | Caminho | Comportamento implementado |
|---|---|---|
| `POST` | `/transacoes` | Valida cartão, senha e saldo; debita o cartão e registra a transação na mesma transação de banco. |

Exemplo de corpo:

```json
{
  "numeroCartao": "1234567890123456",
  "senhaCartao": "1234",
  "valor": 125.00
}
```

Uma autorização bem-sucedida retorna HTTP 201. As recusas de negócio retornam HTTP 422 com um destes códigos no corpo: `VALOR_INVALIDO`, `CARTAO_INEXISTENTE`, `SENHA_INVALIDA` ou `SALDO_INSUFICIENTE`. O saldo precisa ser estritamente maior que o valor da compra; portanto, uma transação que deixaria saldo zero é recusada.

## Configuração e execução

O projeto requer JDK 21 e Docker Compose para iniciar o banco MySQL local. A configuração do datasource em `src/main/resources/application.yaml` corresponde ao serviço `mysql` do Compose (porta 3306, banco `miniautorizador`, usuário `root` sem senha); use credenciais próprias e configuração externa em ambientes compartilhados ou de produção.

No Windows:

```powershell
docker compose up -d mysql
./mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
docker compose up -d mysql
./mvnw spring-boot:run
```

Executar os testes:

```powershell
./mvnw.cmd test
```

O teste de contexto carrega a aplicação completa (`@SpringBootTest`), portanto precisa de um datasource acessível ou de uma configuração de teste isolada. O arquivo de configuração atual usa `ddl-auto: create`, que recria as tabelas ao iniciar; a associação `transacao.cartao_id` é criada pelo Hibernate.

## Testes

- `CartoesControllerTest`: verifica códigos HTTP, corpo da resposta e chamadas ao serviço para cadastro e consulta de cartões.
- `TransacaoControllerTest`: verifica o mapeamento de autorizações e recusas para as respostas HTTP.
- `TransacaoServiceTest`: verifica o débito, a gravação da transação e as regras de recusa sem persistência.
- `AutorizadorApplicationTests`: verifica se o contexto Spring inicia.

Os testes unitários usam Mockito para substituir dependências dos controllers e serviços, permitindo testar suas decisões sem acessar o banco. O teste de contexto é uma verificação de integração mais ampla.

## Observações conhecidas

- `CartoesControllerTest` não está alinhado com a implementação atual: os testes configuram `findByNumeroCartao`, mas o controller consulta `findByNumeroCartaoAndSenha`. Além disso, o caso de cartão existente espera HTTP 201 e chamada a `save`, enquanto o controller retorna HTTP 422 nesse ramo. Corrija o stub e confirme a regra esperada para cadastro duplicado antes de considerar essa cobertura confiável.
- `ddl-auto: create` recria as tabelas na inicialização. Para ambientes que precisam preservar dados, use migrações de banco e uma estratégia como `validate`.
- A autenticação usa um usuário em memória de demonstração. O PIN do cartão é armazenado com BCrypt, mas credenciais fixas de autenticação não são adequadas para produção.
- O auditing JPA do Spring Data está habilitado na aplicação e nas entidades `Cartao` e `Transacao`, permitindo preencher `createdAt` com `@CreatedDate`.
- Os valores monetários são representados como `BigDecimal` para evitar erros de precisão de ponto flutuante.
