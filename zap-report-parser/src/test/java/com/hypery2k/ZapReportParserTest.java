package com.hypery2k;

import br.com.softplan.security.zap.api.report.ZapReport;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.testng.Assert.assertTrue;

public class ZapReportParserTest {

    @Test
    public void shouldReadRiskCodeCorrectly() throws Exception {
        int riskCode1 = new ZapReportParser().getHighestRiskCode(resourceToString("high-risk-report.xml"));
        assertTrue(riskCode1 == 8);
        int riskCode2 = new ZapReportParser().getHighestRiskCode(resourceToString("low-risk-report.xml"));
        assertTrue(riskCode2 == 1);
    }

    @Test
    public void shouldGetCorrectRiskCodeFromReport() throws Exception {
        final String xmlReport = resourceToString("high-risk-report.xml");
        final ZapReport report = new ZapReport(null, xmlReport.getBytes(), new ArrayList<String>());
        int riskCode = new ZapReportParser().getHighestRiskCode(report);
        assertTrue(riskCode == 8);
    }


    private String resourceToString(String filePath) throws IOException, URISyntaxException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath)) {
            return IOUtils.toString(inputStream);
        }
    }
}