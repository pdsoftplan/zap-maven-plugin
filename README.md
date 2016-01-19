# ZAP Maven Plugin

[![Build Status](https://travis-ci.org/pdsoftplan/zap-maven-plugin.svg?branch=master)](https://travis-ci.org/pdsoftplan/zap-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/br.com.softplan.security.zap/zap-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/br.com.softplan.security.zap/zap-maven-plugin)

> Check out the [ZAP SonarQube Plugin](https://github.com/pdsoftplan/sonar-zap)

This plugin makes it easier to integrate [OWASP Zed Attack Proxy (ZAP)](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project) security tests with the application development and build process for Maven users. With this plugin, you can:

- Run ZAP analysis during the build of your application;
- Run authenticated analysis on [CAS](http://jasig.github.io/cas/) applications and on many other applications with complex authentication strategies;
- Use your [Selenium](http://www.seleniumhq.org) integration tests navigation to feed ZAP;
- Easily run ZAP analysis during development.

## Contents

- [Usage](#usage)
- [Configuration Parameters](#configuration-parameters)
- [Authentication Strategies](#authentication-strategies)
- [Examples](#examples)
    - [Using a running instance of ZAP](#using-a-running-instance-of-zap)
    - [Starting ZAP automatically](#starting-zap-automatically)
    - [Starting ZAP with Docker](#starting-zap-with-docker)
    - [Form authentication example](#form-authentication-example)
    - [CAS authentication example](#cas-authentication-example)
- [Selenium Integration](#selenium-integration)

## Usage

Generally, the plugin configuration will follow the format below:

```xml
<plugin>
    <groupId>br.com.softplan.security.zap</groupId>
    <artifactId>zap-maven-plugin</artifactId>
    <version>${zap-maven-plugin.version}</version>
    <configuration>
        <!-- Configuration parameters -->
    </configuration>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals><goal>analyze</goal></goals>
        </execution>
    </executions>
</plugin>
```

> It is necessary to define the phase where the plugin will be executed, as well as the goal that will be executed. Optionally, the plugin can be executed by directly calling the desired goal:
> ```
> mvn br.com.softplan.security.zap:zap-maven-plugin:analyze
> ```

The main goal provided is *analyze*, responsible to execute a ZAP analysis according to the configuration parameters. However, the plugin also provides other goals for more specific situations. The list of available goals is presented bellow:

- *analyze*: performs a complete analysis running (by default) the [Spider](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsSpider) before the [Active Scan](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsAscan) and starting ZAP automatically if necessary (and closing it after the analysis).
- *startZap*: simply starts ZAP (via local installation or Docker).
- *seleniumAnalyze*: assumes ZAP is already executing and simply runs the Active Scan, closing ZAP after the analysis. This goal is useful when there are [Selenium](http://www.seleniumhq.org) integration tests that are executed with a proxy to ZAP and the navigation done by the tests should be used instead of the Spider. More on that at [Selenium Integration](#selenium-integration).

The goals that run analysis save the generated reports in the end of the plugin execution. By default, the reports are saved in the directory `target/zap-reports` within the project. The parameter *reportPath* can be used to specify another directory (absolute or relative).

## Configuration Parameters

**Analysis parameters:**

Parameter | Description | Required? | Default
--- | --- | --- | ---
targetUrl                      | URL of the application that will be scanned  | Yes | -
spiderStartingPointUrl         | Starting point URL for the Spider (and AJAX Spider, in case it runs) | No | *targetUrl*
activeScanStartingPointUrl     | Starting point URL for the Active Scan       | No  | *targetUrl*
analysisTimeoutInMinutes       | Analysis timeout in minutes                  | No  | 480
shouldRunAjaxSpider            | Indicates whether ZAP should execute the [AJAX Spider](https://github.com/zaproxy/zap-core-help/wiki/HelpAddonsSpiderAjaxConcepts) after the default Spider (it can improve the scan on applications that rely on AJAX)     | No  | false
shouldRunPassiveScanOnly       | In case it's true, the Active Scan will not be executed | No | false
shouldStartNewSession          | Indicates whether a new session should be started on ZAP before the analysis | No | true

> If both *spiderStartingPoint* and *activeScanStartingPoint* are provided, *targetUrl* will be ignored. These options are useful when you want to spider through the whole application, but want to run the Active Scan for only a portion of it. 

**ZAP related parameters:**

Parameter | Description | Required? | Default
--- | --- | --- | ---
zapPort              | Port where ZAP is running or will run                                       | Yes | -
zapHost              | Host where ZAP is running                                                   | No  | localhost
zapApiKey            | API key needed to access ZAP's API, in case it's enabled                    | No  | -
zapPath              | Absolute path where ZAP is installed, used to automatically start ZAP       | No  | -
shouldRunWithDocker  | Indicates whether ZAP should be automatically started with Docker           | No  | false
zapOptions           | Options that will be used to automatically start ZAP                        | No  | See bellow
initializationTimeoutInMillis | ZAP's automatic initialization timeout in milliseconds    | No  | 120
reportPath  | Absolute or relative path where the generated reports will be saved         | No  | ${user.dir}/target/zapReports

> To automatically start ZAP, it must be installed locally and the option *zapPath* must be provided. To start ZAP with Docker, Docker must be locally installed and the option *shouldRunWithDocker* must be passed as *true*. In both cases, by default, ZAP is initialized with the following options:
> ```
> -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0 -port ${zapPort}
> ```
> These options make ZAP start without a GUI, with its API key disabled, able to report errors details via API, and able to be accessed remotely. Besides that, it makes sure ZAP will run on the port specified by the *port* option. These options can be overridden by the *zapOptions* parameter.

**Authentication parameters:**

Parameter | Description | Required?
--- | --- | ---
authenticationType | Define the authentication type: 'form', 'CAS' or 'selenium' | Yes, for authenticated analysis
loginUrl           | Login page URL                                              | Yes, for authenticated analysis
username           | Username used in the authentication                         | Yes, for authenticated analysis
password           | Password used in the authentication                         | Yes, for authenticated analysis
extraPostData      | Used to define any extra parameters that must be passed in the authentication request (e.g. *domain=someDomain&param=value*) | No
loggedInRegex      | Regex that identifies a pattern in authenticated responses (needed to allow re-authentication)     | No
loggedOutRegex     | Regex that identifies a pattern in non-authenticated responses (needed to allow re-authentication) | No
excludeFromScan    | Define the URLs regexs that will be excluded from the scan | No

**CAS only authentication parameter:**

Parameter | Description | Required?
--- | --- | ---
protectedPages | Define the URL of a protected page of the application that will be scanned | Yes, if *authenticationType* is *CAS*

> As it was stated, the option *protectedPage* should have as value the URL of a protected page of the application that will be scanned. For CAS authentication, the login is done directly at the CAS server. Thus, in the first access to the application there will be a redirect to the server, that ends up redirecting the user back to the protected page, since the user is already authenticated. ZAP doesn't support this circular redirect, and because of that the application needs to be accessed at least once before the scan is started. This option defines the URL of a protected page that will be accessed after the authentication and before the scan to make sure the circular redirect won't happen during ZAP's analysis.

**Form and Selenium only authentication parameters:**

Parameter | Description | Required? | Default
--- | --- | --- | ---
usernameParameter  | Name of the request parameter that holds the username | No | username
passwordParameter  | Name of the request parameter that holds the password | No | password

**Selenium only authentication parameters:**

Parameter | Description | Required? | Default
--- | --- | --- | ---
httpSessionTokens  | Any additional session tokens that should be added to ZAP prior authentication | No | -
seleniumDriver     | The web driver that will be used to perform authentication: 'html_unit', 'firefox' or 'chrome' | No | firefox

> It's important to realize that the `HtmlUnitDriver` lacks complete support for JavaScript. Therefore, it might not always work properly.

Notice that the parameters *excludeFromScan*, *protectedPages* and *httpSessionTokens* accept multiple values, like in the example below:

```xml
<excludeFromScan>
    <!-- It doesn't matter how you name the inner tag, as long as you remain consistent -->
    <param>http://myapp/logout</param>
    <param>http://myapp/forbidden</param>
</excludeFromScan>
<protectedPages>
    <protectedPage>http://myapp/protected/index</protectedPage>
</protectedPages>
<httpSessionTokens>
    <token>LtpaToken2</token>
</httpSessionTokens>
```

## Authentication Strategies

There are three ways to perform authenticated scans with the ZAP Maven Plugin. The first and most simple one is the form based authentication. This should be used for very simple form authentications (like the one found in the [bodgeit](https://github.com/psiinon/bodgeit) application), where all you need to authenticate is a simple POST request. This strategy uses ZAP's form authentication mechanism, thus reauthentication is possible (through *loggedIn* and *loggedOutRegex* parameters).

It's also possible to run authenticated scans on applications that use [CAS](http://jasig.github.io/cas/). This strategy uses ZAP's script authentication mechanism with a script to perform the CAS authentication. It might not work for all possible CAS configurations (there are many), and as with the form authentication, reauthentication is possible.

The last strategy uses [Selenium](http://www.seleniumhq.org). The idea is to perform the authentication via Selenium, and pass to ZAP the session created (i.e. the session cookie). This should work in most situations, including more complex form based authentications. However, reauthentication is not possible.

## Examples

### Using a running instance of ZAP

For this to work, ZAP must already be running.

```xml
<plugin>
    <groupId>br.com.softplan.security.zap</groupId>
    <artifactId>zap-maven-plugin</artifactId>
    <version>${zap-maven-plugin.version}</version>
    <configuration>
        <zapHost>localhost</zapHost>
        <zapPort>8080</zapPort>
        <targetUrl>http://localhost:8090/testwebapp</targetUrl>
    </configuration>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals><goal>analyze</goal></goals>
        </execution>
    </executions>
</plugin>
```

### Starting ZAP automatically

For ZAP to be automatically started, the option *zapPath* must be provided with the directory where ZAP is installed.

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapPort>8080</zapPort>
		<targetUrl>http://localhost:8090/testwebapp</targetUrl>
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

> There is no need to inform the host in this case, since it will obviously be *localhost*.

### Starting ZAP with Docker

If ZAP is not installed, you can still start ZAP with Docker. For this, Docker must be installed and the option *shouldRunWithDocker* must be provided with value *true*.

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapPort>8080</zapPort>
		<targetUrl>http://localhost:8090/testwebapp</targetUrl>
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

### Form authentication example

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapHost>localhost</zapHost>
    	<zapPort>8080</zapPort>
		<targetUrl>http://localhost:8180/bodgeit</targetUrl>

		<authenticationType>form</authenticationType>
		<username>user</username>
		<password>pass</password>
		<loginUrl>http://localhost:8180/bodgeit/login.jsp</loginUrl>
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

> Notice that it might be necessary to use the tag *![CDATA[]]* so the characters used within the parameter value are not parsed as part of the XML.

### CAS authentication example

```xml
<plugin>
	<groupId>br.com.softplan.security.zap</groupId>
	<artifactId>zap-maven-plugin</artifactId>
	<version>${zap.maven.plugin.version}</version>
	<configuration>
		<zapHost>localhost</zapHost>
		<zapPort>8080</zapPort>
		<targetUrl>https://localhost:8443/myapp</targetUrl>

        <authenticationType>cas</authenticationType>
        <username>user</username>
        <password>pass</password>
        <loginUrl>https://localhost:8443/cas-server/login</loginUrl>
        <protectedPages>
            <protectedPage>https://localhost:8443/myapp/protected/index</protectedPage>
        </protectedPages>
        <loggedOutRegex><![CDATA[\\QLocation: https://localhost:8443/cas-server/\\E.*]]></loggedOutRegex>
	</configuration>
	<executions>
		<execution>
			<phase>verify</phase>
			<goals><goal>analyze</goal></goals>
		</execution>
	</executions>
</plugin>
```

> A good way to achieve re-authentication with CAS is defining the *loggedOutRegex* with a value like `\QLocation: https://your.domain/your-cas-server\E.*`. Unauthenticated responses will be redirects to the CAS server, so this is the easiest way to identify that there was a redirection to the CAS server and thus the user is not logged in.

## Selenium Integration

If your application has [Selenium](http://www.seleniumhq.org) integration tests that navigate through the application, it might be interesting to feed ZAP with the visited pages instead of relying on ZAP's Spider. The Spider can't ensure a complete navigation through the application. Besides that, by feeding ZAP with the visited pages, it's possible to define the analysis scope, since ZAP's tests will only be executed on the pages that were visited during the tests.

The goals *startZap* and *seleniumAnalyze* were developed because of this. With them, it's possible to start ZAP before the integration tests and execute the analysis after the tests, using the navigation done by the tests.

The first step to feed ZAP with your integration tests navigation is to ensure the tests use ZAP as a proxy. This way, all requests made during the tests execution go through ZAP. The configuration needed for the most common drivers are presented bellow:

```java
    WebDriver driver;

    // HtmlUnit
    driver = new HtmlUnitDriver();
	((HtmlUnitDriver) driver).setProxy(ZAP_HOST, ZAP_PORT);
	
	// Firefox
	FirefoxProfile profile = new FirefoxProfile();
	profile.setPreference("network.proxy.type", 1); // 1 = 'Manual proxy configuration'
	profile.setPreference("network.proxy.share_proxy_settings", true);
	profile.setPreference("network.proxy.no_proxies_on", "");
    profile.setPreference("network.proxy.http", ZAP_HOST);
    profile.setPreference("network.proxy.http_port", ZAP_PORT);
    driver = new FirefoxDriver(profile);
	
	// Chrome
	Proxy proxy = new Proxy(); // org.openqa.selenium.Proxy
	proxy.setHttpProxy(ZAP_HOST + ":" + ZAP_PORT);
	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	capabilities.setCapability("proxy", proxy);
	driver = new ChromeDriver(capabilities);
```

With that done, all that remains are the plugin goals configuration:

```xml
<plugin>
    <groupId>br.com.softplan.security.zap</groupId>
    <artifactId>zap-maven-plugin</artifactId>
    <version>${zap.maven.plugin.version}</version>
    <configuration>
		<!-- whatever configuration -->
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

:zap:
