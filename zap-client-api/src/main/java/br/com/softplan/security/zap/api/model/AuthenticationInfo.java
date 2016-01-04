package br.com.softplan.security.zap.api.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import br.com.softplan.security.zap.api.authentication.AuthenticationInfoValidator;

/**
 * Class with all the information needed for authentication (CAS and form based).
 * 
 * @author pdsec
 */
public final class AuthenticationInfo {

	private static final String DEFAULT_LOGIN_REQUEST_DATA = "username={%username%}&password={%password%}";
	
	private AuthenticationType type;
	private String loginUrl;
	private String username;
	private String password;
	private String extraPostData;
	private String loggedInRegex;
	private String loggedOutRegex;
	private String[] excludeFromScan;
	private String[] protectedPages;
	private String protectedPagesSeparatedByComma;
	private String loginRequestData;

	public static Builder builder() {
		return new Builder();
	}
	
	public AuthenticationType getType() {
		return type;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public String getExtraPostData() {
		return extraPostData;
	}
	
	public String getFullLoginRequestData() {
		if (extraPostData != null) {
			return loginRequestData + "&" + extraPostData; 			
		}
		return loginRequestData;
	}

	public String getLoggedInRegex() {
		return loggedInRegex;
	}
	
	public String getLoggedOutRegex() {
		return loggedOutRegex;
	}

	public String[] getExcludeFromScan() {
		return excludeFromScan;
	}
	
	public String[] getProtectedPages() {
		return protectedPages;
	}

	public String getProtectedPagesSeparatedByComma() {
		return protectedPagesSeparatedByComma;
	}
	
	public String getLoginRequestData() {
		return loginRequestData;
	}
	
	public static class Builder {
		
		private AuthenticationType type;
		private String loginUrl;
		private String username;
		private String password;
		private String extraPostData;
		private String loggedInRegex;
		private String loggedOutRegex;
		private String[] excludeFromScan;
		private String[] protectedPages;
		private String protectedPagesSeparatedByComma;
		private String loginRequestData = DEFAULT_LOGIN_REQUEST_DATA;
		
		/**
		 * Builds an {@code AuthenticationInfo} instance with the minimum information required for CAS authentication.
		 * <p>
		 * The {@code protectedPages} parameter is needed so we can access them after the authentication and before ZAP's
		 * analysis. This is required to avoid circular redirections during the scan, not supported by ZAP.
		 * 
		 * @param loginUrl the login URL (i.e. {@code http://myapp.com/login}).
		 * @param username the username that will be authenticated.
		 * @param password the user's password.
		 * @param protectedPages an array of URLs with at least one protected page for each context that will be analyzed.
		 * @return the built {@link AuthenticationInfo} instance.
		 */
		public AuthenticationInfo buildCasAuthenticationInfo(String loginUrl, String username, String password, String... protectedPages) {
			return type(AuthenticationType.CAS)
					.loginUrl(loginUrl)
					.username(username)
					.password(password)
					.protectedPages(protectedPages)
					.build();
		}
		
		/**
		 * Builds an {@code AuthenticationInfo} instance with the minimum information required for form authentication.
		 * 
		 * @param loginUrl the login URL (i.e. {@code http://myapp.com/login}).
		 * @param username the username that will be authenticated.
		 * @param password the user's password.
		 * @return the built {@link AuthenticationInfo} instance.
		 */
		public AuthenticationInfo buildFormAuthenticationInfo(String loginUrl, String username, String password) {
			return type(AuthenticationType.FORM)
					.loginUrl(loginUrl)
					.username(username)
					.password(password)
					.build();
		}
		
		/**
		 * Sets the {@link AuthenticationType}.
		 * 
		 * @param type the {@link AuthenticationType} ({@code CAS} or {@code FORM}).
		 * @return this {@code Builder} instance.
		 */
		public Builder type(AuthenticationType type) {
			this.type = type;
			return this;
		}
		
		/**
		 * Sets the login URL. <b>This is a required parameter.</b>
		 * 
		 * @param loginUrl the login URL (i.e. {@code http://myapp.com/login}).
		 * @return this {@code Builder} instance.
		 */
		public Builder loginUrl(String loginUrl) {
			this.loginUrl = loginUrl;
			return this;
		}
		
		/**
		 * Sets the username. <b>This is a required parameter.</b>
		 * 
		 * @param username the username that will be authenticated.
		 * @return this {@code Builder} instance.
		 */
		public Builder username(String username) {
			this.username = username;
			return this;
		}
		
		/**
		 * Sets the password. <b>This is a required parameter.</b>
		 * 
		 * @param password the user's password.
		 * @return this {@code Builder} instance.
		 */
		public Builder password(String password) {
			this.password = password;
			return this;
		}
		
		/**
		 * Sets any extra post data needed for the authentication.
		 * 
		 * @param extraPostData extra post data that will be sent in the login request (i.e. {@code domain=someDomain&param=value}).
		 * @return this {@code Builder} instance.
		 */
		public Builder extraPostData(String extraPostData) {
			this.extraPostData = extraPostData;
			return this;
		}
		
		/**
		 * Sets the logged in regex, thus enabling reauthentication.
		 * <p>
		 * This regex should match logged in response messages, so ZAP is able reauthenticate
		 * in case it logs itself out during the scan.
		 * <p>
		 * <b>There is no need to set both {@code loggedInRegex} and {@code loggedOutRegex} options, only one is enough.</b>
		 * 
		 * @param loggedInRegex the regex that matches logged in response messages.
		 * @return this {@code Builder} instance.
		 */
		public Builder loggedInRegex(String loggedInRegex) {
			this.loggedInRegex = loggedInRegex;
			return this;
		}
		
		/**
		 * Sets the logged out regex, thus enabling reauthentication.
		 * <p>
		 * This regex should match logged in response messages, so ZAP is able reauthenticate
		 * in case it logs itself out during the scan.
		 * <p>
		 * <b>There is no need to set both {@code loggedInRegex} and {@code loggedOutRegex} options, only one is enough.</b>
		 * 
		 * @param loggedOutRegex the regex that matches logged out response messages.
		 * @return this {@code Builder} instance.
		 */
		public Builder loggedOutRegex(String loggedOutRegex) {
			this.loggedOutRegex = loggedOutRegex;
			return this;
		}
		
		/**
		 * Sets the URLs that will not be scanned.
		 * 
		 * @param excludeFromScan an array of URLs to be excluded from the scan.
		 * @return this {@code Builder} instance.
		 */
		public Builder excludeFromScan(String... excludeFromScan) {
			this.excludeFromScan = excludeFromScan;
			return this;
		}
		
		/**
		 * <b>This is a required parameter for CAS authentication.</b> 
		 * <p>
		 * For CAS authentication, the login is done directly at the CAS server. So, when accessing the app
		 * for the first time, the user will be redirected to the CAS server, which will redirect the user back to the app
		 * since the user is authenticated. ZAP doesn't support circular redirection, thus it can't handle this process.
		 * <p>
		 * The URLs defined here will be accessed once after the authentication and before the scan, triggering the
		 * circular redirections and avoiding them to happen during the analysis.
		 * 
		 * @param protectedPages an array of URLs with at least one protected page for each context that will be analyzed.
		 * @return this {@code Builder} instance.
		 */
		public Builder protectedPages(String... protectedPages) {
			this.protectedPages = protectedPages;
			if (protectedPages != null) {
				protectedPagesSeparatedByComma = StringUtils.join(protectedPages, ","); 
			}
			return this;
		}
		
		/**
		 * Sets the login request data used for form authentication.
		 * 
		 * @param loginRequestData the login request data for form based authentication 
		 *                         (default: <code>username=&#123;%username%&#125;&amp;password=&#123;%password%&#125;</code>).
		 * @return this {@code Builder} instance.
		 */
		public Builder loginRequestData(String loginRequestData) {
			if (loginRequestData != null) {
				this.loginRequestData = loginRequestData;
			}
			return this;
		}
		
		/**
		 * Validates and builds an {@link AuthenticationInfo} instance based on the builder parameters.
		 * 
		 * @return a {@link AuthenticationInfo} instance.
		 */
		public AuthenticationInfo build() {
			AuthenticationInfo authenticationInfo = new AuthenticationInfo(this);
			AuthenticationInfoValidator.validate(authenticationInfo);
			return authenticationInfo;
		}
		
	}
	
	private AuthenticationInfo(Builder builder) {
		this.type                           = builder.type;
		this.loginUrl                       = builder.loginUrl;
		this.username                       = builder.username;
		this.password                       = builder.password;
		this.extraPostData                  = builder.extraPostData;
		this.loggedInRegex                  = builder.loggedInRegex;
		this.loggedOutRegex                 = builder.loggedOutRegex;
		this.excludeFromScan                = builder.excludeFromScan;
		this.protectedPages                 = builder.protectedPages;
		this.protectedPagesSeparatedByComma = builder.protectedPagesSeparatedByComma;
		this.loginRequestData               = builder.loginRequestData;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("type", type.name())
				.append("loginUrl", loginUrl)
				.append("username", username)
				.append("password", password)
				.append("extraPostData", extraPostData)
				.append("loggedInRegex", loggedInRegex)
				.append("loggedOutRegex", loggedOutRegex)
				.append("excludeFromScan", excludeFromScan)
				.append("protectedPages", protectedPages)
				.append("loginRequestData", loginRequestData)
				.toString();
	}

}
