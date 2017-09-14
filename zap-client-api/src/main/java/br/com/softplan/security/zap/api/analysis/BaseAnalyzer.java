package br.com.softplan.security.zap.api.analysis;

import br.com.softplan.security.zap.api.exception.ZapAnalysisTimeoutException;
import br.com.softplan.security.zap.api.exception.ZapClientException;
import br.com.softplan.security.zap.api.model.AnalysisInfo;
import br.com.softplan.security.zap.api.model.ScanType;
import br.com.softplan.security.zap.api.report.ZapReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;
import org.zaproxy.clientapi.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Base implementation of {@link Analyzer}.
 * <p>
 * Responsible for the timeout logic and calls to the ZAP API.
 *
 * @author pdsec
 */
public abstract class BaseAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAnalyzer.class);

    private static final long SPIDER_STATUS_POLLING_INTERVAL_IN_SECONDS = 1;
    private static final long AJAX_SPIDER_STATUS_POLLING_INTERVAL_IN_SECONDS = 10;
    private static final long ACTIVE_SCAN_STATUS_POLLING_INTERVAL_IN_SECONDS = 5;

    private String apiKey;
    private ClientApi api;

    private long analysisTimeoutInMillis;
    private long analysisStartTime;

    private boolean spiderDidRun = false;

    public BaseAnalyzer(String apiKey, ClientApi api) {
        this.api = api;
        this.apiKey = apiKey;
    }

    protected void init(long analysisTimeoutInMillis) {
        this.analysisTimeoutInMillis = analysisTimeoutInMillis;
        this.analysisStartTime = System.currentTimeMillis();
    }

    protected void runSpider(AnalysisInfo analysisInfo) {
        String targetUrl = analysisInfo.getSpiderStartingPointUrl();
        LOGGER.info("{} : {}", ScanType.SPIDER, targetUrl);

        try {
            ApiResponse resp = api.spider.scan(targetUrl, "", "", "", "");
            String scanId = ((ApiResponseElement) resp).getValue();

            waitForScanToFinish(scanId, ScanType.SPIDER);
            spiderDidRun = true;
        } catch (ClientApiException e) {
            handleError("Error running Spider.", e);
        }
    }

    protected void runAjaxSpider(AnalysisInfo analysisInfo) {
        String targetUrl = analysisInfo.getSpiderStartingPointUrl();
        LOGGER.info("{} : {}", ScanType.AJAX_SPIDER, targetUrl);

        try {
            ApiResponse resp = api.ajaxSpider.scan(apiKey, targetUrl, "false");
            String scanId = ((ApiResponseElement) resp).getValue();

            waitForScanToFinish(scanId, ScanType.AJAX_SPIDER);
        } catch (ClientApiException e) {
            handleError("Error running Ajax Spider.", e);
        }
    }

    @SuppressWarnings("restriction")
    protected void runActiveScan(AnalysisInfo analysisInfo) {
        String targetUrl = analysisInfo.getActiveScanStartingPointUrl();
        LOGGER.info("{} : {}", ScanType.ACTIVE_SCAN, targetUrl);

        try {
            ApiResponse resp = api.ascan.scan(apiKey, targetUrl, "True", "True", "", "", "");
            String scanId = ((ApiResponseElement) resp).getValue();

            waitForScanToFinish(scanId, ScanType.ACTIVE_SCAN);
        } catch (ClientApiException e) {
            // TODO: rever essa estratégia de tratamento
            String message = "Error running Active Scan.";
            if (e.getCause() instanceof SAXParseException || e.getCause() instanceof com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException
                    || e.getMessage().contains("URL Not Found in the Scan Tree")) {
                message += " One possible cause to this problem is that ZAP crashes when you try to run the Active Scan without any prior navigation made on the target "
                        + "(i.e. Spidering or proxied navigation were not done before on the Active Scan target).";
            }
            handleError(message, e);
        }
    }

    protected ZapReport generateReport() {
        LOGGER.info("Retrieving reports and Spider results...");
        ZapReport zapReport = null;

        try {
            byte[] htmlReport = api.core.htmlreport(apiKey);
            byte[] xmlReport = api.core.xmlreport(apiKey);
            List<String> spiderResults = getAndLogSpiderResults();

            zapReport = new ZapReport(htmlReport, xmlReport, spiderResults);
            LOGGER.info("Reports retrieved.");
        } catch (ClientApiException e) {
            handleError("Error retrieving reports.", e);
        }

        return zapReport;
    }

    private List<String> getAndLogSpiderResults() {
        List<String> urls = new ArrayList<String>();
        if (!spiderDidRun) {
            LOGGER.info("Spider did not run.");
            return urls;
        }
        try {
            ApiResponseList responseList = (ApiResponseList) api.spider.results("");
            LOGGER.debug("Visited URLs by the Spider:");
            for (ApiResponse response : responseList.getItems()) {
                String url = ((ApiResponseElement) response).getValue();
                LOGGER.debug("    {}", url);
                urls.add(url);
            }
        } catch (ClientApiException e) {
            handleError("Error retrieving Spider results.", e);
        }
        return urls;
    }

    private void waitForScanToFinish(String scanId, ScanType scanType) {
        int progress = 0;
        final int maxProgress = 100;

        while (progress < maxProgress) {
            switch (scanType) {
                case SPIDER:
                    progress = waitSpider(scanId, progress);
                    break;
                case AJAX_SPIDER:
                    progress = waitAjaxSpider(scanId, progress);
                    break;
                case ACTIVE_SCAN:
                    progress = waitActiveScan(scanId, progress);
            }
            LOGGER.info("{} progress : {}%", scanType, progress);
        }
        LOGGER.info("{} complete", scanType);
    }

    private int waitSpider(String scanId, int progress) {
        sleep(SPIDER_STATUS_POLLING_INTERVAL_IN_SECONDS);
        int newProgress = progress;
        try {
            newProgress = Integer.parseInt(((ApiResponseElement) api.spider.status(scanId)).getValue());
        } catch (NumberFormatException | ClientApiException e) {
            handleError("Error retrieving Spider progress.", e);
        }

        if (scanTimeoutReached(scanId)) {
            try {
                api.spider.stop(apiKey, scanId);
                LOGGER.info("Spider STOPPED");
            } catch (ClientApiException e) {
                handleError("Error stopping Active Scan.", e);
            }

            String message = "Analysis did not finish before the timeout (" + analysisTimeoutInMillis + " ms).";
            LOGGER.error(message);
            throw new ZapAnalysisTimeoutException(message);
        }
        return newProgress;
    }

    private int waitAjaxSpider(String scanId, int progress) {
        sleep(AJAX_SPIDER_STATUS_POLLING_INTERVAL_IN_SECONDS);
        int newProgress = progress;
        try {
            newProgress = getAjaxSpiderProgress();
        } catch (ClientApiException e) {
            handleError("Error retrieving Ajax Spider progress.", e);
        }

        if (scanTimeoutReached(scanId)) {
            try {
                api.ajaxSpider.stop(scanId);
                LOGGER.info("AJAX Spider STOPPED");
            } catch (ClientApiException e) {
                handleError("Error stopping Active Scan.", e);
            }

            String message = "Analysis did not finish before the timeout (" + analysisTimeoutInMillis + " ms).";
            LOGGER.error(message);
            throw new ZapAnalysisTimeoutException(message);
        }
        return newProgress;
    }

    private int waitActiveScan(String scanId, int progress) {
        sleep(ACTIVE_SCAN_STATUS_POLLING_INTERVAL_IN_SECONDS);
        int newProgress = progress;
        try {
            newProgress = Integer.parseInt(((ApiResponseElement) api.ascan.status(scanId)).getValue());
        } catch (NumberFormatException | ClientApiException e) {
            handleError("Error retrieving Active Scan progress.", e);
        }

        if (scanTimeoutReached(scanId)) {
            try {
                api.ascan.stop(apiKey, scanId);
                LOGGER.info("Active Scan STOPPED");
            } catch (ClientApiException e) {
                handleError("Error stopping Active Scan.", e);
            }

            String message = "Analysis did not finish before the timeout (" + analysisTimeoutInMillis + " ms).";
            LOGGER.error(message);
            throw new ZapAnalysisTimeoutException(message);
        }
        return newProgress;
    }

    private void handleError(String message, Throwable cause) {
        LOGGER.error(message, cause);
        throw new ZapClientException(message, cause);
    }

    private boolean scanTimeoutReached(String scanId) {
        long elapsedTimeInMillis = System.currentTimeMillis() - analysisStartTime;
        return elapsedTimeInMillis >= analysisTimeoutInMillis;
    }

    private int getAjaxSpiderProgress() throws ClientApiException {
        String progress = ((ApiResponseElement) api.ajaxSpider.status()).getValue();
        final int maxProgress = 100;
        return progress.equalsIgnoreCase("running") ? 0 : maxProgress;
    }

    private static void sleep(long seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error(e.getMessage(), e);
        }
    }

}
