# ZAP Client API
---

## Sumário

- [Introdução](#introdução)
- [Importando o Projeto](#importando-o-projeto)
- [API](#api)
    - [ZapInfo](#zapinfo)
    - [AuthenticationInfo](#authenticationinfo)
    - [AnalysisInfo](#analysisinfo)
    - [ZapReport](#zapreport)
- [Exemplos](#exemplos)

## Introdução

O ZAP Client API é um projeto construído em cima da [API Java do ZAP](https://github.com/zaproxy/zaproxy/wiki/ApiJava).
Ele abstrai todas as chamadas à API do ZAP, simplificando significativamente o processo de configuração e execução de testes para aplicações que queiram utilizar o ZAP.
Além disso, o ZAP Client API adiciona suporte à autenticação CAS, nao suportada por padrão pelo ZAP ou por sua API.

## Importando o Projeto

Para utilizar a API, basta adicionar a seguinte dependência no POM do projeto:

```xml
<dependency>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-client-api</artifactId>
	<version>${zap.client.api.version}</version>
</dependency>
```

As versões disponíveis podem ser encontradas na [página com as tags do projeto](../../tags).

## API

A classe principal da API é a 
[`ZapClient`](zap-client-api/src/main/java/br/com/softplan/security/zap/api/ZapClient.java), 
que recebe uma instância de 
[`ZapInfo`](zap-commons/src/main/java/br/com/softplan/security/zap/commons/ZapInfo.java) 
em seu construtor e, opcionalmente, uma instância de 
[`AuthenticationInfo`](zap-client-api/src/main/java/br/com/softplan/security/zap/api/model/AuthenticationInfo.java) 
para análises que terão autenticação. `ZapClient` possui apenas um método público: `analyze()`. 
Esse método inicia uma análise no ZAP e recebe como parâmetro uma instância de 
[`AnalysisInfo`](zap-client-api/src/main/java/br/com/softplan/security/zap/api/model/AnalysisInfo.java).

A utilização da API se resume na construção dos objetos mencionados e na chamada ao método `analyze()`. A saída da análise (e retorno do método `analyze()`)
são os relatórios gerados pelo ZAP, representados por uma instância da classe
[`ZapReport`](zap-client-api/src/main/java/br/com/softplan/security/zap/api/report/ZapReport.java).

### ZapInfo

Essa classe armazena todas as informações necessárias referentes à instância do ZAP que está executando ou que será executada.
Um builder é disponibilizado para facilitar a construção de objetos com três métodos principais:

- `buildToUseRunningZap()`
- `buildToRunZap()`
- `buildToRunZapWithDocker()`

Como fica claro a partir dos nomes dos métodos, além de ser possível executar uma análise em um ZAP que já está em execução,
o ZAP Client API é capaz de inicializar automaticamente o ZAP, inclusive via Docker, para a execução da análise. Nessas opções, o ZAP
é automaticamente finalizado após a análise.

Opcionalmente, também é possível definir o valor de cada parâmetro através do builder. Seguem alguns exemplos de construção do objeto:

```java
// Para utilizar uma instância do ZAP que já está em execução, é necessário passar o host e a porta do ZAP
ZapInfo runningZapInfo = ZapInfo.builder().buildToUseRunningZap("localhost", 8090);

// Para iniciar o ZAP automaticamente, não é necessário informar o host, mas é preciso passar o diretório em que o ZAP está instalado
ZapInfo runZapInfo = ZapInfo.builder().buildToRunZap(8090, "C:\\ZAP");

// Para rodar o ZAP via Docker, o Docker deve estar instalado localmente e a aplicação deve ter permissão para executar o Docker
ZapInfo dockerZapInfo = ZapInfo.builder().buildToRunZapWithDocker(8090);

// Os atributos também podem ser definidos manualmente (dockerZapInfo é equivalente a newDockerZapInfo, por exemplo)
ZapInfo newDockerZapInfo = ZapInfo.builder().port(8090).shouldRunWithDocker(true).build();

// Para que o ZAP seja iniciado automaticamente, basta passar um valor para o atributo 'path'
ZapInfo newRunZapInfo = ZapInfo.builder().port(8090).path("C:\\ZAP").build(); // equivalente a runZapInfo

// Por fim, as duas estratégias podem se misturar
ZapInfo runningZapInfoWithTimeout = ZapInfo.builder().initializationTimeoutInMillis(30000L).buildToRunZap(8090, "C:\\ZAP");
```

### AuthenticationInfo

Da mesma forma que `ZapInfo` armazena todas as informações necessárias referentes ao ZAP, `AuthenticationInfo` armazena todos os parâmetros
necessários para autenticação. De forma análoga, um builder também é disponibilizado para facilitar a criação dos objetos com dois métodos principais:

- `buildCasAuthenticationInfo()`
- `buildFormAuthenticationInfo()`

Esses métodos facilitam a criação de instâncias de `AuthenticationInfo` para cada um dos tipos de autenticação suportados: CAS e form. Ambos os métodos
recebem como parâmetro o mínimo necessário para cada tipo de autenticação, mas em geral será necessário fornecer parâmetros adicionais (para habilitar reautenticação, por exemplo).
Existem diversos parâmetros que podem ser utilizados para configurar a autenticação, seguem alguns exemplos explorando os casos mais comuns:

```java
// Na autenticação via form, os parâmetros obrigatórios são apenas loginUrl, username e password
AuthenticationInfo formInfo = AuthenticationInfo.builder()
        .buildFormAuthenticationInfo("http://myapp/login", "username", "password");

// Para autenticação via CAS, também é necessário informar uma página protegida para cada contexto que será analisado
// Essa página será acessada automaticamente após a autenticação e antes do scan do ZAP, evitando redirecionamentos durante o scan
AuthenticationInfo casInfo = AuthenticationInfo.builder()
        .buildCasAuthenticationInfo("http://myapp/login", "username", "password", "http://mydomain/myapp/protected/somePage");

// Para habilitar reautenticação (garantindo que toda a análise será autenticada), basta definir valores para loggedInRegex ou loggedOutRegex
AuthenticationInfo formReauthInfo = AuthenticationInfo.builder()
        .loggedInRegex("\\Q<a href=\"logout.jsp\">Logout</a>\\E")
        .buildFormAuthenticationInfo("http://myapp/login", "username", "password");

// É possível definir páginas que não serão scaneadas (útil para excluir páginas de logout, caso reautenticação não seja possível)
AuthenticationInfo formExcludeInfo = AuthenticationInfo.builder()
        .excludeFromScan("http://myapp/logout")
        .buildFormAuthenticationInfo("http://myapp/login", "username", "password");
```

### AnalysisInfo

Essa pequena classe armazena as informações referentes à análise:

- `targetUrl`: URL da aplicação que será scaneada;
- `analysisTimeoutInMinutes`: timeout da análise;
- `analysisType`: tipo da análise. Existem três tipos disponíveis: `WITH_SPIDER`, `WITH_AJAX_SPIDER` e `ACTIVE_SCAN_ONLY`.

Os tipos da análise estão definidos no enum
[`AnalysisType`](zap-client-api/src/main/java/br/com/softplan/security/zap/api/model/AnalysisType.java).
`WITH_SPIDER` é a análise padrão, que executa o [Spider do ZAP](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsSpider) antes do [Active Scan](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsAscan).
Para aplicações que utilizam AJAX, pode ser interessante executar o AJAX Spider disponível após o Spider padrão. O tipo `WITH_AJAX_SPIDER` define esse comportamento.
Por fim, `ACTIVE_SCAN_ONLY` executa apenas o Active Scan, para casos em que a navegação pela aplicação foi feita através de proxy com o ZAP via testes com o Selenium, por exemplo
(informações sobre esse tipo de estratégia são apresentadas na [página do projeto exemplo](zap-example#an%C3%A1lise-integrada-com-selenium)).

Todos os parâmetros são passados no construtor e o objeto criado é passado para o método `analyze` do `ZapClient`. Portanto,
é possível executar diferentes análises a partir de uma mesma instância de `ZapClient`.

### ZapReport

O retorno do `analyze()` é um objeto do tipo `ZapReport`. Através dele é possível acessar os relatórios gerados pelo ZAP.
Além disso, o ZAP Client API gera um novo relatório que apresenta as URLs visitadas pelo Spider.
Esse relatório é importante porque ele demonstra se o Spider conseguiu, de fato, navegar pela aplicação. Dependendo dos resultados, pode ser possível
concluir que a autenticação não funcionou corretamente, por exemplo.

## Exemplos

Execução de análise em um ZAP que já está em execução:

```java
ZapInfo zapInfo = ZapInfo.builder().buildToUseRunningZap("localhost", 8080);
ZapClient zapClient = new ZapClient(zapInfo);
ZapReport zapReport = zapClient.analyze(new AnalysisInfo("http://server17:8180/bodgeit", 120));

System.out.println(zapReport.getHtmlReportAsString());
System.out.println(zapReport.getHtmlSpiderResultsAsString());
```

Execução de análise iniciando o ZAP automaticamente com autenticação via CAS:

```java
ZapInfo zapInfo = ZapInfo.builder().buildToUseRunningZap("localhost", 8080);
AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
        .loggedOutRegex("\\QLocation: https://server119.softplan.com.br:8443/bouncer-server/\\E.*")
        .buildCasAuthenticationInfo(
            "https://server119.softplan.com.br:8443/bouncer-server/login", 
            "bob", 
            "foo", 
            "https://server119.softplan.com.br:8443/bouncer-mock-saj/protected/index.jsp"
        );
ZapClient zapClient = new ZapClient(zapInfo, authenticationInfo);
ZapReport zapReport = zapClient.analyze(new AnalysisInfo("http://server17:8180/bodgeit", 120));

System.out.println(zapReport.getHtmlReportAsString());
System.out.println(zapReport.getHtmlSpiderResultsAsString());
```

---
:zap: