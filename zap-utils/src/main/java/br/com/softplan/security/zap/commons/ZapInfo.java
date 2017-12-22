package br.com.softplan.security.zap.commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class that represents the information about the ZAP instance that will be used.
 * <p>
 * Depending on how the instance of this class is built, you end up with one of these situations:
 * <ul>
 * <li>ZAP is up and running in a given {@code host} and {@code port};</li>
 * <li>ZAP is locally installed and will be automatically started (and stopped afterwards).</li>
 * <li>Docker is locally installed and ZAP's image will be automatically started (and stopped afterwards).</li>
 * </ul>
 *
 * @author pdsec
 * @see Builder#buildToUseRunningZap(String, int) Builder().buildToUseRunningZap()
 * @see Builder#buildToRunZap(int, String, String) Builder().buildToRunZap()
 * @see Builder#buildToRunZapWithDocker(int, String) Builder().buildToRunZapWithDocker()
 */
public final class ZapInfo {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_KEY = "";
    private static final Long DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS = new Long(120000);
    private static final String DEFAULT_JVM_OPTIONS = "-Xmx512m";
    public static final String DEFAULT_OPTIONS = "-daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0";

    private String host;
    private Integer port;
    private String apiKey;
    private String path;
    private String jmvOptions;
    private String options;
    private int failingRiskCode;
    private Long initializationTimeoutInMillis;
    private boolean shouldRunWithDocker;

    public static Builder builder() {
        return new Builder();
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getPort() {
        return port;
    }

    public int getFailingRiskCode() {
        return failingRiskCode;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getJmvOptions() {
        return jmvOptions;
    }

    public String getOptions() {
        return options;
    }

    public long getInitializationTimeoutInMillis() {
        return initializationTimeoutInMillis;
    }

    public boolean shouldRunWithDocker() {
        return shouldRunWithDocker;
    }

    public static class Builder {

        private String host = DEFAULT_HOST;
        private Integer port;
        private int failingRiskCode;
        private String apiKey = DEFAULT_KEY;
        private String path;
        private String jmvOptions = DEFAULT_JVM_OPTIONS;
        private String options = DEFAULT_OPTIONS;
        private Long initializationTimeoutInMillis = DEFAULT_INITIALIZATION_TIMEOUT_IN_MILLIS;
        private boolean shouldRunWithDocker;

        /**
         * Use this if ZAP is up and running (locally or in a remote machine).
         *
         * @param host the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @param port the port where ZAP is running (e.g. {@code 8080}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToUseRunningZap(String host, int port) {
            return host(host).port(port).build();
        }

        /**
         * Use this if ZAP is up and running (locally or in a remote machine).
         *
         * @param host   the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @param port   the port where ZAP is running (e.g. {@code 8080}).
         * @param apiKey the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToUseRunningZap(String host, int port, String apiKey) {
            return host(host).port(port).apiKey(apiKey).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port the port where ZAP will run (e.g. {@code 8080}).
         * @param path the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(int port, String path) {
            return port(port).path(path).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param path    the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@code -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0}
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(int port, String path, String options) {
            return port(port).path(path).options(options).build();
        }

        /**
         * Use this if ZAP is installed and you want ZAP to be started and stopped automatically.
         * <p>
         * <b>ZAP must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param path    the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@code -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0}
         * @param apiKey  the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZap(int port, String path, String options, String apiKey) {
            return port(port).path(path).options(options).apiKey(apiKey).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port the port where ZAP will run (e.g. {@code 8080}).
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(int port) {
            return shouldRunWithDocker(true).port(port).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@code -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0}
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(int port, String options) {
            return shouldRunWithDocker(true).port(port).options(options).build();
        }

        /**
         * Use this if Docker is installed and you want to use ZAP from its Docker image.
         * <p>
         * <b>Docker must be installed locally for this to work.</b>
         *
         * @param port    the port where ZAP will run (e.g. {@code 8080}).
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@code -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0}
         * @param apiKey  the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return the built {@link ZapInfo} instance.
         */
        public ZapInfo buildToRunZapWithDocker(int port, String options, String apiKey) {
            return shouldRunWithDocker(true).port(port).options(options).apiKey(apiKey).build();
        }

        /**
         * Sets the host where ZAP is running. Don't call this if you want ZAP to be started automatically.
         *
         * @param host the host where ZAP is running (e.g. {@code localhost}, {@code 172.23.45.13}).
         * @return this {@code Builder} instance.
         */
        public Builder host(String host) {
            if (host != null) {
                this.host = host;
            }
            return this;
        }

        /**
         * Either sets the port where ZAP is running or the port where ZAP will run, if ZAP is to be started automatically.
         * <p>
         * If the {@code host} was set, then the {@code port} represents where ZAP is currently running on the host.
         * Otherwise, it represents the {@code port} where ZAP will run (locally or in a Docker image).
         *
         * @param port the port where ZAP is running or where ZAP will run (e.g. {@code 8080}).
         * @return this {@code Builder} instance.
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder failingRiskCode(int failingRiskCode) {
            this.failingRiskCode = failingRiskCode;
            return this;
        }

        /**
         * Sets the API key needed to access ZAP's API, in case the key is enabled (it is by default).
         *
         * @param apiKey the API key needed to use ZAP's API, if the key is enabled. It can be found at ZAP - Tools - Options - API.
         * @return this {@code Builder} instance.
         */
        public Builder apiKey(String apiKey) {
            if (apiKey != null) {
                this.apiKey = apiKey;
            }
            return this;
        }

        /**
         * Sets the path where ZAP is installed.
         * <p>
         * This should be used when ZAP is installed locally, so the API is able to automatically start ZAP.
         *
         * @param path the path where ZAP is installed (e.g. {@code C:\Program Files (x86)\OWASP\Zed Attack Proxy}).
         * @return this {@code Builder} instance.
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the JVM options used to run ZAP.
         * <p>
         * This should be used when ZAP is installed locally and it is automatically started and stopped.
         *
         * @param jmvOptions the JVM options used to start ZAP.
         * @return this {@code Builder} instance.
         * @see #path(String)
         */
        public Builder jmvOptions(String jmvOptions) {
            this.jmvOptions = jmvOptions;
            return this;
        }

        /**
         * Sets the options used to start ZAP.
         * <p>
         * This should be used to overwrite the default options used to start ZAP (locally or in a Docker image).
         *
         * @param options the options that will be used to start ZAP. This is an optional parameter, the default options used are:
         *                {@code -daemon -config api.disablekey=true -config api.incerrordetails=true -config proxy.ip=0.0.0.0}
         * @return this {@code Builder} instance.
         */
        public Builder options(String options) {
            if (options != null) {
                this.options = options;
            }
            return this;
        }

        /**
         * Sets the timeout in milliseconds for ZAP's initialization, if ZAP is to be started automatically.
         * The default value is {@code 120000}.
         *
         * @param initializationTimeoutInMillis the timeout in milliseconds for ZAP's initialization.
         * @return this {@code Builder} instance.
         */
        public Builder initializationTimeoutInMillis(Long initializationTimeoutInMillis) {
            if (initializationTimeoutInMillis != null) {
                this.initializationTimeoutInMillis = initializationTimeoutInMillis;
            }
            return this;
        }

        /**
         * Use this to indicate that ZAP should be started automatically with Docker. This is {@code false} by default.
         *
         * @param shouldRunWithDocker {@code true} if ZAP should be automatically started with Docker, {@code false} otherwise.
         * @return this {@code Builder} instance.
         */
        public Builder shouldRunWithDocker(boolean shouldRunWithDocker) {
            this.shouldRunWithDocker = shouldRunWithDocker;
            return this;
        }

        /**
         * Builds a {@link ZapInfo} instance based on the builder parameters.
         * <p>
         * You should probably use the other build methods, choosing the one that suits your needs.
         *
         * @return a {@link ZapInfo} instance.
         * @see #buildToUseRunningZap(String, int) buildToUseRunningZap()
         * @see #buildToRunZap(int, String, String) buildToRunZap()
         * @see #buildToRunZapWithDocker(int, String) buildToRunZapWithDocker()
         */
        public ZapInfo build() {
            return new ZapInfo(this);
        }

    }

    private ZapInfo(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.apiKey = builder.apiKey;
        this.path = builder.path;
        this.jmvOptions = builder.jmvOptions;
        this.options = builder.options;
        this.initializationTimeoutInMillis = builder.initializationTimeoutInMillis;
        this.shouldRunWithDocker = builder.shouldRunWithDocker;
        this.failingRiskCode = builder.failingRiskCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("host", host)
                .append("port", port)
                .append("failingRiskCode", failingRiskCode)
                .append("apiKey", apiKey)
                .append("path", path)
                .append("jvmOptions", jmvOptions)
                .append("options", options)
                .append("initializationTimeout", initializationTimeoutInMillis)
                .append("shouldRunWithDocker", shouldRunWithDocker)
                .toString();
    }

}
