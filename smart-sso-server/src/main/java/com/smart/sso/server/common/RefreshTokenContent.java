package com.smart.sso.server.common;

import com.smart.sso.server.enums.ClientTypeEnum;

public class RefreshTokenContent extends AccessTokenContent {

	private static final long serialVersionUID = -1332598459045608781L;

	private String accessToken;
	
	private String clientId;

	public RefreshTokenContent(String tgt, ClientTypeEnum clientType, String redirectUri, String accessToken,
			String clientId) {
		super(tgt, clientType, redirectUri);
		this.accessToken = accessToken;
		this.clientId = clientId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}