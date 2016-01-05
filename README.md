# ZAP Maven Plugin

> Check out the [ZAP SonarQube Plugin](https://github.com/pdsoftplan/sonar-zap)

This plugin was developed to make it easier to integrate [OWASP Zed Attack Proxy (ZAP)](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project) security tests with the application development and build process for Maven users. With this plugin, you can:

- Run ZAP analysis during the build of your application;
- Run authenticated analysis on [CAS](http://jasig.github.io/cas/) applications;
- Use your Selenium integration tests navigation to feed ZAP;
- Easily run ZAP analysis during development.

## Usage

Generally, the plugin configuration will follow the format below:

```xml
<plugin>
    <groupId>br.com.softplan.security.zap</groupId>
    <artifactId>zap-maven-plugin</artifactId>
    <version>${zap.maven.plugin.version}</version>
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

- *analyze*: performs a complete analysis running the [Spider](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsSpider) before the [Active Scan](https://github.com/zaproxy/zap-core-help/wiki/HelpStartConceptsAscan) and starting ZAP automatically if necessary (and closing it after the analysis).
- *startZap*: simply starts ZAP (via local installation or Docker).
- *seleniumAnalyze*: assumes ZAP is already executing and simply runs the Active Scan, closing ZAP after the analysis. This goal is useful when there are Selenium integration tests that are executed with a proxy to ZAP and the navigation done by the tests should be used instead of the Spider. More on that at [Selenium Integration](#selenium-integration).

The goals that run analysis save the generated reports in the end of the plugin execution. By default, the reports are saved in the directory `target/zap-reports` within the project. The parameter *reportPath* can be used to specify another directory (absolute or relative).

## Configuration Parameters

Analysis parameters:

Parameter | Description | Required? | Default
--- | --- | --- | ---
target                   | URL of the application that will be scanned  | Yes | -
analysisTimeoutInMinutes | Analysis timeout in minutes                  | No  | 480
shouldRunAjaxSpider      | Indicates whether ZAP should execute the [AJAX Spider](https://github.com/zaproxy/zap-core-help/wiki/HelpAddonsSpiderAjaxConcepts) after the default Spider (it can improve the scan on applications that rely on AJAX)     | No  | false

ZAP related parameters:

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

> To automatically start ZAP, it must be installed locally and the option *zapPath* must be provided. To use ZAP with Docker, Docker must be locally installed and the option *shouldRunWithDocker* must be passed as *true*. In both cases, by default, ZAP is initialized with the following options:
> ```
> -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0 -port ${zapPort}
> ```
> These options make ZAP start without a GUI, with its API key disabled, able to report errors details via API, and able to be accessed remotely. Besides that, it makes sure ZAP will run on the port specified by the *port* option. These options can be overridden by the *zapOptions* parameter.

Authentication parameters:

Parameter | Description | Required?
--- | --- | ---
authenticationType | Define the authentication type: 'form' or 'CAS' | Yes, for authenticated analysis
loginUrl           | Login page URL                                  | Yes, for authenticated analysis
username           | Username used in the authentication             | Yes, for authenticated analysis
password           | Password used in the authentication             | Yes, for authenticated analysis
extraPostData      | Used to define any extra parameters that must be passed in the authentication request (e.g. *domain=someDomain&param=value*) | No
loggedInRegex      | Regex that identifies a pattern in authenticated responses (needed to allow re-authentication)     | No
loggedOutRegex     | Regex that identifies a pattern in non-authenticated responses (needed to allow re-authentication) | No
excludeFromScan    | Define the URLs that will be excluded from the scan | No

CAS only authentication parameter:

Parameter | Description | Required?
--- | --- | ---
protectedPages | Define the URL of a protected page of the application that will be scanned | Yes, if *authenticationType* is *CAS*

> As it was stated, the option *protectedPage* should have as value the URL of a protected page of the application that will be scanned. For CAS authentication, the login is done directly at the CAS server. Thus, in the first access to the application there will be a redirect to the server, that ends up redirecting the user back to the protected page, since the user is already authenticated. ZAP doesn't support this circular redirect, and because of that the application needs to be accessed at least once before the scan is started. This option defines the URL of a protected page that will be accessed after the authentication and before the scan to make sure the circular redirect won't happen during ZAP's analysis.

Form only authentication parameters:

Parameter | Description | Required? | Default
--- | --- | --- | ---
usernameParameter  | Name of the request parameter that holds the username | No | username
passwordParameter  | Name of the request parameter that holds the password | No | password

Notice that the parameters *excludeFromScan* and *protectedPages* accept multiple values, like in the example below:

```xml
<excludeFromScan>
    <param>http://myapp/logout</param>
    <param>http://myapp/forbidden</param>
</excludeFromScan>
<protectedPages>
    <param>http://myapp/protected/index</param>
</protectedPages>
```

## Examples

## Selenium Integration

:zap:
