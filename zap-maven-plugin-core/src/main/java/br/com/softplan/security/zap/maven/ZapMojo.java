package br.com.softplan.security.zap.maven;

import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AnalysisType;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.model.SeleniumDriver;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.api.report.ZapReportUtil;
import br.com.softplan.security.zap.commons.ZapInfo;
import com.hypery2k.ZapReportParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Abstract Mojo used as a base for the other ZAP Mojos.
 *
 * @author pdsec
 * @author hypery2k
 */
public abstract class ZapMojo extends AbstractMojo {

    // Common
    /**
     * Disables the plug-in execution.
     */
    @Parameter(property = "zap.skip", defaultValue = "false")
    private boolean skip;

    // Analysis
    /**
     * URL of the application that will be scanned.
     */
    @Parameter(required = true)
    private String targetUrl;

    /**
     * number for risk code at which the plugin is failing the build, see https://www.owasp.org/index.php/OWASP_Risk_Rating_Methodology#Step_4:_Determining_the_Severity_of_the_Risk
     */
    @Parameter(defaultValue = "10")
    private int failingRiskCodeThreshold;

    /**
     * Starting point URL for the Spider (and AJAX Spider, in case it runs).
     */
    @Parameter
    private String spiderStartingPointUrl;

    /**
     * Starting point URL for the Active Scan.
     */
    @Parameter
    private String activeScanStartingPointUrl;

    /**
     * The URLs to be set on ZAP's context (absolute or relative).
     */
    @Parameter
    private String[] context;

    @Parameter
    private String[] technologies;

    /**
     * Analysis timeout in minutes.
     */
    @Parameter(defaultValue = "480")
    private int analysisTimeoutInMinutes;

    /**
     * Indicates whether ZAP should execute the AJAX Spider after the default Spider (it can improve the scan on applications that rely on AJAX).
     */
    @Parameter(defaultValue = "false")
    private boolean shouldRunAjaxSpider;

    /**
     * In case it's true, the Active Scan will not be executed.
     */
    @Parameter(defaultValue = "false")
    private boolean shouldRunPassiveScanOnly;

    /**
     * Indicates whether a new session should be started on ZAP before the analysis.
     */
    @Parameter(defaultValue = "true")
    private boolean shouldStartNewSession;

    // ZAP
    /**
     * Port where ZAP is running or will run.
     */
    @Parameter(required = true)
    private Integer zapPort;

    /**
     * Host where ZAP is running.
     */
    @Parameter(defaultValue = "localhost")
    private String zapHost;

    /**
     * API key needed to access ZAP's API, in case it's enabled.
     */
    @Parameter(defaultValue = "")
    private String zapApiKey;

    /**
     * Absolute path where ZAP is installed, used to automatically start ZAP.
     */
    @Parameter
    private String zapPath;

    /**
     * JVM options used to launch ZAP.
     */
    @Parameter(defaultValue = "-Xmx512m")
    private String zapJvmOptions;

    /**
     * Options that will be used to automatically start ZAP.
     */
    @Parameter(defaultValue = ZapInfo.DEFAULT_OPTIONS)
    private String zapOptions;

    /**
     * Indicates whether ZAP should be automatically started with Docker.
     */
    @Parameter(defaultValue = "false")
    private boolean shouldRunWithDocker;

    /**
     * ZAP's automatic initialization timeout in milliseconds.
     */
    @Parameter(defaultValue = "120000")
    private Integer initializationTimeoutInMillis;

    /**
     * Absolute or relative path where the generated reports will be saved.
     */
    @Parameter(defaultValue = "${project.build.directory}/zap-reports")
    private File reportPath;

    // Authentication
    /**
     * Define the authentication type: 'http', 'form', 'cas' or 'selenium'.
     */
    @Parameter
    private String authenticationType;

    /**
     * Login page URL.
     */
    @Parameter
    private String loginUrl;

    /**
     * Username used in the authentication.
     */
    @Parameter
    private String username;

    /**
     * Password used in the authentication.
     */
    @Parameter
    private String password;

    /**
     * Used to define any extra parameters that must be passed in the authentication request (e.g. domain=someDomain&amp;param=value)
     */
    @Parameter
    private String extraPostData;

    /**
     * Regex that identifies a pattern in authenticated responses (needed to allow re-authentication).
     */
    @Parameter
    private String loggedInRegex;

    /**
     * Regex that identifies a pattern in non-authenticated responses (needed to allow re-authentication).
     */
    @Parameter
    private String loggedOutRegex;

    /**
     * Define the URLs regexs that will be excluded from the scan.
     */
    @Parameter
    private String[] excludeFromScan;

    // CAS
    /**
     * Define the URL of a protected page of the application that will be scanned.
     */
    @Parameter
    private String[] protectedPages;

    // Form and Selenium
    /**
     * Name of the request parameter that holds the username.
     */
    @Parameter(defaultValue = "username")
    private String usernameParameter;

    /**
     * Name of the request parameter that holds the password.
     */
    @Parameter(defaultValue = "password")
    private String passwordParameter;

    /**
     * Any additional session tokens that should be added to ZAP prior authentication.
     */
    @Parameter
    private String[] httpSessionTokens;

    /**
     * The web driver that will be used to perform authentication: 'htmlunit', 'firefox', or 'phantomjs'.
     */
    @Parameter(defaultValue = "firefox")
    private String seleniumDriver;

    // HTTP
    /**
     * The host name of the server where the authentication is done.
     */
    @Parameter
    private String hostname;

    /**
     * The realm the credentials apply to.
     */
    @Parameter
    private String realm;

    /**
     * The port of the server where the authentication is done.
     */
    @Parameter(defaultValue = "80")
    private int port;

    protected ZapInfo buildZapInfo() {
        return ZapInfo.builder()
                .host(zapHost)
                .port(zapPort)
                .failingRiskCode(failingRiskCodeThreshold)
                .apiKey(zapApiKey)
                .path(zapPath)
                .jmvOptions(zapJvmOptions)
                .options(zapOptions)
                .initializationTimeoutInMillis((long) initializationTimeoutInMillis)
                .shouldRunWithDocker(shouldRunWithDocker)
                .build();
    }

    protected AuthenticationInfo buildAuthenticationInfo() {
        if (authenticationType == null) {
            return null;
        }
        return AuthenticationInfo.builder()
                .type(authenticationType)
                .loginUrl(loginUrl)
                .username(username)
                .password(password)
                .extraPostData(extraPostData)
                .loggedInRegex(loggedInRegex)
                .loggedOutRegex(loggedOutRegex)
                .excludeFromScan(excludeFromScan)
                .protectedPages(protectedPages)
                .usernameParameter(usernameParameter)
                .passwordParameter(passwordParameter)
                .loginRequestData()
                .httpSessionTokens(httpSessionTokens)
                .seleniumDriver(SeleniumDriver.valueOf(seleniumDriver.toUpperCase()))
                .hostname(hostname)
                .realm(realm)
                .port(port)
                .build();
    }

    protected AnalysisInfo buildAnalysisInfo() {
        AnalysisType analysisType = AnalysisType.WITH_SPIDER;
        if (shouldRunAjaxSpider && shouldRunPassiveScanOnly) {
            analysisType = AnalysisType.SPIDER_AND_AJAX_SPIDER_ONLY;
        } else {
            if (shouldRunAjaxSpider) {
                analysisType = AnalysisType.WITH_AJAX_SPIDER;
            }
            if (shouldRunPassiveScanOnly) {
                analysisType = AnalysisType.SPIDER_ONLY;
            }
        }
        return buildAnalysisInfo(analysisType);
    }

    protected AnalysisInfo buildAnalysisInfo(AnalysisType analysisType) {
        return AnalysisInfo.builder()
                .targetUrl(targetUrl)
                .spiderStartingPointUrl(spiderStartingPointUrl)
                .activeScanStartingPointUrl(activeScanStartingPointUrl)
                .context(context)
                .technologies(technologies)
                .analysisTimeoutInMinutes(analysisTimeoutInMinutes)
                .analysisType(analysisType)
                .shouldStartNewSession(shouldStartNewSession)
                .build();
    }

    protected void saveReport(ZapReport zapReport) {
        getLog().info("Saving Reports...");
        if (reportPath != null) {
            ZapReportUtil.saveAllReports(zapReport, reportPath.getAbsolutePath());
        } else {
            ZapReportUtil.saveAllReports(zapReport);
        }
    }


    protected void analyzeReport(ZapReport zapReport, ZapInfo zapInfo) throws MojoExecutionException {
        int riskCode = new ZapReportParser().getHighestRiskCode(zapReport);
        if (riskCode > zapInfo.getFailingRiskCode()) {
            throw new MojoExecutionException("Detected too high risk code");
        }
    }

    protected String getTargetUrl() {
        return this.targetUrl;
    }

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("Zap is skipped.");
            return;
        }

        doExecute();
    }

    public abstract void doExecute() throws MojoExecutionException, MojoFailureException;

}
