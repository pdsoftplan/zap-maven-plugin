package br.com.softplan.security.zap.commons.boot;

import br.com.softplan.security.zap.commons.exception.ZapInitializationTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Base implementation for {@link ZapBoot}, responsible for the timeout logic.
 *
 * @author pdsec
 */
public abstract class AbstractZapBoot implements ZapBoot {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractZapBoot.class);

  private static final String HEAD = "HEAD";

  static final long ZAP_INITIALIZATION_POLLING_INTERVAL_IN_MILLIS = 5 * 1000L;

  static final String DEFAULT_ZAP_OPTIONS = "-daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0";

  // If ZAP is automatically started, its log will be stored in [current working directory]/target/zap-reports, along with the generated reports
  static final String DEFAULT_ZAP_LOG_PATH = System.getProperty("user.dir") + File.separator + "target" + File.separator + "zap-reports";
  static final String DEFAULT_ZAP_LOG_FILE_NAME = "zap.log";

  static boolean isZapRunning(int port) {
    return isZapRunning("localhost", port);
  }

  static boolean isZapRunning(String host, int port) {
    return getResponseFromZap(host, port) == HttpURLConnection.HTTP_OK;
  }

  static int getResponseFromZap(String host, int port) {
    if (host == null) {
      return -1;
    }

    String url = "http://" + host + ":" + port;

    int responseCode = -1;
    try {
      HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setRequestMethod(HEAD);
      responseCode = conn.getResponseCode();
    }
    catch (ConnectException e) {
      LOGGER.debug("ZAP could not be reached at {}:{}.", host, port);
    }
    catch (IOException e) {
      LOGGER.error("Error trying to get a response from ZAP.", e);
    }
    return responseCode;
  }

  static void waitForZapInitialization(int port, long timeoutInMillis) {
    waitForZapInitialization("localhost", port, timeoutInMillis);
  }

  static void waitForZapInitialization(String host, int port, long timeoutInMillis) {
    long startUpTime = System.currentTimeMillis();
    do {
      if (System.currentTimeMillis() - startUpTime > timeoutInMillis) {
        String message = "ZAP did not start before the timeout (" + timeoutInMillis + " ms).";
        LOGGER.error(message);
        throw new ZapInitializationTimeoutException(message);
      }

      sleep(ZAP_INITIALIZATION_POLLING_INTERVAL_IN_MILLIS);
      LOGGER.info("Checking if ZAP has started at {}:{}...", host, port);
    } while (!isZapRunning(host, port));

    LOGGER.info("ZAP has started!");
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error(e.getMessage(), e);
    }
  }

}
