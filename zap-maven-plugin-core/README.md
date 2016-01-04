# ZAP Maven Plugin
---

## Sumário

- [Introdução](#introdução)
- [Parâmetros de Configuração](#parâmetros-de-configuração)
- [Definindo o Plugin no POM](#definindo-o-plugin-no-pom)
- [Exemplos](#exemplos)
    - [Configurando com uma instância do ZAP em execução](#configurando-com-uma-instância-do-zap-em-execução)
    - [Configurando para iniciar o ZAP automaticamente](#configurando-para-iniciar-o-zap-automaticamente)
    - [Iniciando o ZAP via Docker](#iniciando-o-zap-via-docker)
    - [Configurando autenticação via form](#configurando-autenticação-via-form)
    - [Configurando autenticação via CAS](#configurando-autenticação-via-cas)
- [Integração com Selenium](#integração-com-selenium)

## Introdução

O ZAP Maven Plugin foi desenvolvido como uma forma prática de adicionar os testes do ZAP nos projetos. 
O plugin invoca o [ZAP Client API](https://gitlab.softplan.com.br/PD-PesquisaArquitetura/zap-client/) com os parâmetros especificados no *pom.xml* da aplicação, 
que por sua vez invoca o ZAP para rodar os testes e gerar o relatório.

## Parâmetros de Configuração

A listagem de todos os parâmetros para configuração do plugin pode ser encontrada na página do [ZAP CLI](zap-cli). 
Todas as opções do CLI estão disponíveis no plugin com os mesmos nomes, com exceção das opções *ajaxSpider* e *docker*, 
que foram substituidas respectivamente pelas opções *shouldRunAjaxSpider* e *shouldRunWithDocker*, que recebem como entrada um valor `Boolean` (*true* ou *false*).

Da mesma forma que no CLI é possível passar mais de uma vez as opções *excludeFromScan* e *protectedPage* para a definição de múltiplos valores, 
no plugin, as opções *excludeFromScan* e *protectedPages* (agora no plural) recebem uma lista de elementos, como no exemplo abaixo:

```xml
<excludeFromScan>
    <param>https://server119.softplan.com.br:8443/bouncer-mock-saj/logout</param>
    <param>https://server119.softplan.com.br:8443/bouncer-server/logout</param>
</excludeFromScan>
<protectedPages>
    <param>https://server119.softplan.com.br:8443/bouncer-mock-saj/protected/index.jsp</param>
</protectedPages>
```

Além disso, para o caso do plugin, o parâmetro *reportPath* também funciona com caminho relativo, além de absoluto.

## Definindo o plugin no POM

Em geral, a configuração do plugin no POM do projeto segue o seguinte modelo:

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<!-- Parâmetros de configuração -->
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

> É necessário definir a *phase* em que o plugin será executado e o *goal*.
> Opcionalmente o plugin pode ser executado da seguinte forma:
> ```
> mvn br.com.softplan.security.zap:zap-maven-plugin:analyze
> ```

O principal *goal* fornecido pelo plugin é o *analyze*, responsável por executar uma análise no ZAP de acordo com os parâmetros de configuração.
Entretanto, o plugin também disponibiliza outros *goals* para situações mais específicas. A lista dos *goals* disponíveis é apresentada abaixo:

- *analyze*: realiza a análise completa executando o [Spider](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsSpider) 
antes do [Active Scan](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsAscan) e inicializando o ZAP automaticamente caso seja necessário
(e finalizando-o após a análise).
- *startZap*: apenas inicializa o ZAP (via Docker ou não).
- *seleniumAnalyze*: assume que o ZAP já está executando e roda apenas o Active Scan, finalizando o ZAP após a análise. Esse *goal* é útil para o caso
em que os testes de integração da aplicação são executados com o proxy do ZAP e a navegação realizada durante os testes deve ser utilizada ao invés do Spider.
Mais sobre isso na seção [Integração com Selenium](#integração-com-selenium).

Os *goals* que executam análises salvam os relatórios gerados no final da execução do plugin.
Por padrão, os relatórios são salvos na pasta `target\zap-reports` dentro do projeto. 
O parâmetro *\<reportPath\>* pode ser passado para especificar um diretório customizado (absoluto ou relativo).

## Exemplos

### Configurando com uma instância do ZAP em execução

Nesse exemplo o ZAP já deve estar sendo executado para a análise funcionar:

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapHost>localhost</zapHost>
		<zapPort>8090</zapPort>
		<target>http://localhost:8080/testwebapp</target>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

### Configurando para iniciar o ZAP automaticamente

Para o ZAP ser iniciado automaticamente, a opção *zapPath* deve ser passada com o diretório em que o ZAP está instalado, como no exemplo abaixo:

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapPort>8090</zapPort>
		<target>http://localhost:8080/testwebapp</target>
		<zapPath>C:\Program Files (x86)\OWASP\Zed Attack Proxy</zapPath>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

> Nesse caso não é necessário informar o *host*. 

### Iniciando o ZAP via Docker

Caso o ZAP não esteja instalado na máquina, 
a análise ainda pode ser realizada através do Docker. Para isso, o Docker deve estar instalado
e a opção *shouldRunWithDocker* deve ser passada com o valor *true*:

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapPort>8090</zapPort>
		<target>http://localhost:8080/testwebapp</target>
		<shouldRunWithDocker>true</shouldRunWithDocker>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

### Configurando autenticação via form

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapHost>localhost</zapHost>
    	<zapPort>8080</zapPort>
		<target>http://server17:8180/bodgeit</target>

		<authenticationType>form</authenticationType>
		<username>zaptest@test.com</username>
		<password>zaptest@test.com</password>
		<loginUrl>http://server17:8180/bodgeit/login.jsp</loginUrl>
		<loggedInRegex><![CDATA[\\Q<a href=\"logout.jsp\">Logout</a>\\E]]></loggedInRegex>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

> Dependendo do valor da opção fornecida, pode ser necessário utilizar a tag *![CDATA[]]* para que os caracteres do valor da opção não sejam interpretados como parte do XML.

### Configurando autenticação via CAS

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapHost>localhost</zapHost>
		<zapPort>8080</zapPort>
		<target>https://server119.softplan.com.br:8443/bouncer-mock-saj</target>

        <authenticationType>cas</authenticationType>
        <username>bob</username>
        <password>foo</password>
        <loginUrl>https://server119.softplan.com.br:8443/bouncer-server/login</loginUrl>
        <protectedPage>https://server119.softplan.com.br:8443/bouncer-mock-saj/protected/index.jsp</protectedPage>
        <loggedOutRegex><![CDATA[\\QLocation: https://server119.softplan.com.br:8443/bouncer-server/\\E.*]]></loggedOutRegex>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

> Uma estratégia eficaz para habilitar reautenticação em autenticação via CAS é definir a *loggedOutRegex* com o valor `\QLocation: https://your.domain/your-cas-server\E.*`.
Quando uma página protegida é acessada e o usuário não está autenticado, ele será redirecionado para o servidor CAS. Com essa regex o ZAP será capaz de identificar o redirecionamento.

## Integração com Selenium

Para o caso de aplicações que possuem testes de integração que navegam pela aplicação, 
pode ser interessante alimentar o ZAP com as páginas navegadas ao invés de dependender do Spider do ZAP. 
O Spider não garante a navegação completa pela aplicação e, além disso, dessa forma é possível definir o escopo da análise, 
já que os testes do ZAP só serão executados nas páginas que foram visitadas durante os testes.

Os *goals* *startZap* e *seleniumAnalyze* foram desenvolvidos pensando nesse caso. Com eles, é possível iniciar o ZAP antes dos testes de integração
e executar a análise após os testes, utilizando a navegação que foi realizada. Mais informações sobre essa estratégia, incluindo um exemplo
envolvendo o Cargo Maven Plugin para subir a aplicação automaticamente durante o build, podem ser observadas na página do [ZAP Example](zap-example).

Segue um exemplo de configuração para iniciar o ZAP antes dos testes de integração e executar a análise (sem o Spider) após a execução dos testes:

```xml
<plugin>
    <groupId>br.com.softplan.security.zap</groupId>
    <artifactId>zap-maven-plugin</artifactId>
    <version>${zap.maven.plugin.version}</version>
    <configuration>
		<zapHost>localhost</zapHost>
		<zapPort>8090</zapPort>
		<target>http://localhost:8080/zap-example-web</target>
		<zapPath>C:\Program Files (x86)\OWASP\Zed Attack Proxy</zapPath>
	</configuration>
	<executions>
		<execution>
			<id>start-zap</id>
			<phase>pre-integration-test</phase>
			<goals><goal>startZap</goal></goals>
		</execution>
		<execution>
			<id>selenium-analyze</id>
			<phase>post-integration-test</phase>
			<goals><goal>seleniumAnalyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

---
:zap: