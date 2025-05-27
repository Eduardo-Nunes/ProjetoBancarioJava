# Sistema Bancário - Java

Um sistema bancário simples implementado em Java 17, utilizando arquitetura limpa e banco de dados SQLite local.

## 📋 Funcionalidades

- **Autenticação de Usuários**: Login e registro com hash de senha
- **Gerenciamento de Contas**: Criação e visualização de contas bancárias
- **Operações Bancárias**:
  - Depósito de dinheiro
  - Saque de dinheiro
  - Transferência entre contas
- **Histórico de Transações**: Visualização completa do histórico
- **Persistência de Dados**: Armazenamento local com SQLite

## 🏗️ Arquitetura

O projeto segue o padrão MVC (Model-View-Controller) com arquitetura limpa:

```
src/main/java/com/bank/
├── model/           # Entidades de negócio (User, Account, Transaction)
├── services/        # Camada de acesso a dados (DAOs, DatabaseConnection)
├── controller/      # Lógica de negócio e controladores
└── Main.java        # Ponto de entrada da aplicação
```

### Camadas da Aplicação

1. **Model (Modelo)**: Contém as entidades de domínio
   - `User.java` - Entidade usuário
   - `Account.java` - Entidade conta bancária
   - `Transaction.java` - Entidade transação

2. **Services (Serviços)**: Responsável pela persistência dos dados
   - `DatabaseConnection.java` - Conexão com SQLite
   - `UserDAO.java` - Acesso a dados de usuários
   - `AccountDAO.java` - Acesso a dados de contas
   - `TransactionDAO.java` - Acesso a dados de transações

3. **Controller (Controlador)**: Lógica de negócio
   - `AuthController.java` - Autenticação
   - `AccountController.java` - Operações bancárias
   - `BankSystemController.java` - Interface principal

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

### Instalação e Execução

1. **Clone ou baixe o projeto**

2. **Navegue até o diretório do projeto**
   ```bash
   cd bank-system-java
   ```

3. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

4. **Execute a aplicação**
   ```bash
   mvn exec:java
   ```

   Ou alternativamente:
   ```bash
   mvn clean package
   java -cp target/classes com.bank.Main
   ```

## 🎮 Como Usar

### 1. Tela de Login
- **Opção 1**: Fazer login com usuário existente
- **Opção 2**: Registrar novo usuário
- **Opção 3**: Sair do sistema

### 2. Menu Principal (após login)
- **Visualizar Contas**: Mostra todas as contas do usuário
- **Depositar Dinheiro**: Adiciona valor a uma conta
- **Sacar Dinheiro**: Remove valor de uma conta
- **Transferir Dinheiro**: Move valor entre contas
- **Histórico de Transações**: Mostra todas as transações
- **Criar Nova Conta**: Adiciona uma nova conta ao usuário
- **Logout**: Sair da sessão atual

### Exemplo de Uso

```
=== Bem-vindo ao Sistema Bancário Java ===

--- Menu de Login ---
1. Login
2. Registrar
3. Sair
Escolha uma opção: 1

Usuário: john_doe
Senha: password123
Login realizado com sucesso!

--- Menu Principal ---
Bem-vindo, john_doe!
1. Visualizar Contas
2. Depositar Dinheiro
3. Sacar Dinheiro
4. Transferir Dinheiro
5. Ver Histórico de Transações
6. Criar Nova Conta
7. Logout
Escolha uma opção: 2

Conta: ACC001 | Saldo: R$1000,00
Digite o valor do depósito: R$100
Digite a descrição (opcional): Depósito de teste
Depósito realizado com sucesso! ID da Transação: abc-123-def
```

## 🗄️ Banco de Dados

O sistema utiliza SQLite como banco de dados local:

- **Arquivo**: `bank_system.db` (criado automaticamente)
- **Tabelas**:
  - `users` - Informações dos usuários
  - `accounts` - Contas bancárias
  - `transactions` - Histórico de transações

### Estrutura das Tabelas

**users**
```sql
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TEXT NOT NULL,
    active INTEGER NOT NULL DEFAULT 1
);
```

**accounts**
```sql
CREATE TABLE accounts (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    account_number TEXT UNIQUE NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at TEXT NOT NULL,
    active INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

**transactions**
```sql
CREATE TABLE transactions (
    id TEXT PRIMARY KEY,
    account_id TEXT NOT NULL,
    target_account_id TEXT,
    type TEXT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    description TEXT,
    timestamp TEXT NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

## 🔒 Segurança

- **Hash de Senhas**: Utiliza SHA-256 para hash das senhas
- **Validação de Dados**: Validação rigorosa em todas as operações
- **Transações Atômicas**: Operações de transferência são atômicas
- **Verificação de Saldo**: Impede saques com saldo insuficiente

## 📁 Estrutura de Arquivos

```
bank-system-pure-java/
├── pom.xml                                    # Configuração Maven
├── README.md                                  # Este arquivo
├── bank_system.db                             # Banco SQLite (criado automaticamente)
└── src/
    └── main/
        └── java/
            └── com/
                └── bank/
                    ├── Main.java             # Ponto de entrada
                    ├── model/                # Entidades de domínio
                    │   ├── User.java
                    │   ├── Account.java
                    │   └── Transaction.java
                    ├── services/                 # Camada de serviços
                    │   ├── DatabaseConnection.java
                    │   ├── UserDAO.java
                    │   ├── AccountDAO.java
                    │   └── TransactionDAO.java
                    └── controller/           # Controladores
                        ├── AuthController.java
                        ├── AccountController.java
                        └── BankSystemController.java
```

## 🛠️ Tecnologias Utilizadas

- **Java 17**: Linguagem de programação
- **SQLite**: Banco de dados local
- **Maven**: Gerenciamento de dependências e build
- **JDBC**: Conectividade com banco de dados

## 📝 Dependências

```xml
<dependencies>
    <!-- SQLite Database -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.44.1.0</version>
    </dependency>

    <!-- JUnit para testes -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```