package com.smart.sso.client.filter;

/**
 * 参数注入Filter
 * 
 * @author Joe
 */
public class ParamFilter {

	private String clientId;
	private String clientSecret;
	private String serverUrl;
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
}