package br.com.softplan.security.zap.maven;

import br.com.softplan.security.zap.api.ZapClient;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.AuthenticationInfo;
import br.com.softplan.security.zap.api.report.ZapReport;
import br.com.softplan.security.zap.commons.ZapInfo;
import br.com.softplan.security.zap.commons.boot.Zap;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal to execute a full analysis with ZAP.
 * <p>
 * It will run the default Spider, and optionally the AJAX Spider, proceed with the Active Scan and generate the reports.
 *
 * @author pdsec
 */
@Mojo(name = "analyze")
public class AnalyzeMojo extends ZapMojo {

    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting ZAP analysis at target: " + getTargetUrl());

        ZapInfo zapInfo = buildZapInfo();
        AuthenticationInfo authenticationInfo = buildAuthenticationInfo();
        AnalysisInfo analysisInfo = buildAnalysisInfo();

        ZapClient zapClient = new ZapClient(zapInfo, authenticationInfo);
        try {
            Zap.startZap(zapInfo);
            ZapReport zapReport = zapClient.analyze(analysisInfo);
            saveReport(zapReport);
            analyzeReport(zapReport, zapInfo);
        } finally {
            Zap.stopZap();
        }

        getLog().info("ZAP analysis finished.");
    }

}
