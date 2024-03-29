# Desafio San Giorgio

### Objetivo do API

O caso de uso consiste em desenvolver uma funcionalidade que recebe um objeto contendo o código do vendedor e uma lista
de pagamentos realizados. Cada pagamento é identificado pelo código da cobrança a que ele se refere. O sistema deve
validar se o vendedor e o código da cobrança existem na base de dados. Além disso, ele deve verificar se o pagamento é
parcial, total ou excedente em comparação com o valor original cobrado. Para cada situação de pagamento, o sistema deve
enviar o objeto para uma fila SQS (Simple Queue Service) distinta e retornar o mesmo objeto recebido com a informação do
status de pagamento preenchida.

## Rodando a aplicação

### Pré requisitos:

```text
* Java JDK 17
* Docker
```

## Desenvolvimento

Para rodar a aplicação em modo de desenvolvimento importar o projeto na sua IDE favorita e executar o método `main` na
classe `DesafioSanGiorgioApplication`

## Build

Para gerar um build com os artefatos basta executar o seguinte comando:

Unix:

```
./gradlew build
```

Windows:

```
./gradlew.bat build
```

O artefato será gerado na raiz do projeto no diretório: `build/lib/desafio-san-giorgio-1.0.0.jar`

## Docker

Para rodar a aplicação localmente de maneira rápida e fácil para testes foi criado o arquivo `docker-compose.yml` que
fica na raiz da aplicação.

Nesse arquivo foi declarado a dependência do `Rabbitmq` que a aplicação necessita precisa para rodar corretamente, e
também ele é responsável por fazer o build da imagem da nossa aplicação e subir os containers.

Para criar os containers da nossa `API` e do `Rabbitmq` execute o seguinte comando:
`docker compose up -d`

Para destruir os containers criados anteriormente execute o seguinte comando:
`docker compose down`

## Arquitetura

Para melhor entedimento, legibilidade, manuntenabilidade e para seguirmos os príncipios do SOLID eu escolhi utilizar a
arquitetura limpa(clean architecture) que já é consolidada em projetos Java com Spring Boot e que também pode ser
segregada em vários
micro-serviços sem grandes dificuldades, segue abaixo um esboço dessa arquitetura:

![](https://github.com/MarioJunio/desafio-san-giorgio/blob/master/images/java-clean-architecture.png)

### Rabbitmq (SQS)

Ele atua como um intermediário entre diferentes aplicações, permitindo que elas se comuniquem e transmitam mensagens uns
aos outros de maneira eficiente e confiável.

As mensagens de pagamento são direcionadas para filas diferentes de acordo com seu respectivo status de pagamento: (
parcial, total, excedente), sendo assim foi criado 3 filas sendo elas: parcial_queue, total_queue e excess_queue, cada
mensagem é redirecionada para determinada fila através do routing key(chave de roteamento) quando a mensagem é publicada
no exchange ele verifica a routing key e decide enviar a mensagem para determinada fila.

![](https://github.com/MarioJunio/desafio-san-giorgio/blob/master/images/rabbit_exchange.png)

### H2 Database

Para armazenar os dados de cobrança e pagamento foi utilizado o banco de dados em memória H2, e para acessar a UI de
gerenciamento onde você pode visualizar os dados persistidos nas tabelas, basta acessar a URL abaixo:

```
http://localhost:8080/h2-console/
```

E preencher as informações de acesso conforme a imagem abaixo, e para alterar as credenciais de acesso basta editar o
arquivo <b>src/main/resources/application.properties:</b>

![](https://github.com/MarioJunio/desafio-san-giorgio/blob/master/images/h2.png)

### Flyway

Para o gerenciamento de migrações na base de dados está sendo utilizado o flyway que executa os scripts SQL que estão
dentro da pasta <b>src/main/resources/db/migration</b> na inicialização da aplicação.

### Dados de teste

Os dados de testes são inseridos na base de dados de forma automática pelo flyway no bootstrap da aplicação:

<b>Vendores:</b>
```
id: 1, Nome: João Rineiro
id: 2, Nome: Mario Marques
```

<b>Cobranças:</b>

```
Id: 10, Valor: 30.50, Vendedor: João Ribeiro
Id: 11, Valor: 105.00, Vendedor: João Ribeiro
Id: 12, Valor: 15.00, Vendedor: Mario Marques
Id: 13, Valor: 852.00, Vendedor: Mario Marques
```

### Testes

Para realizar os testes unitários foi utilizado o `mockito` que oferece suporte para criação de mocks e stubs
de uma maneira rápida e eficiente, para rodar os testes basta executar o seguinte comando abaixo na raiz do projeto:

Unix:

```bat
./gradlew test
```

Windows:

```bat
./gradlew.bat test
```

### Considerações finais

Para realizar o processamento das mensagens publicadas nas filas foram criados 3 consumidores, cada um fazendo o
processamento de determinado tipo de fila, e salvando o pagamento realizado na base de dados com o status atualizado.
