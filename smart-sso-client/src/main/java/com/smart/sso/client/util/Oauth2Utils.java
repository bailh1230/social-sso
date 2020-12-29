package com.smart.sso.client.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.smart.sso.client.constant.Oauth2Constant;
import com.smart.sso.client.enums.GrantTypeEnum;
import com.smart.sso.client.rpc.Result;
import com.smart.sso.client.rpc.RpcAccessToken;

/**
 * Oauth2辅助类
 * 
 * @author Joe
 */
public class Oauth2Utils {

	private static final Logger logger = LoggerFactory.getLogger(Oauth2Utils.class);

	/**
	 * 获取accessToken（密码模式，app通过此方式由客户端代理转发http请求到服务端获取accessToken）
	 * 
	 * @param serverUrl
	 * @param clientId
	 * @param clientSecret
	 * @param username
	 * @param password
	 * @return
	 */
	public static Result<RpcAccessToken> getAccessToken(String serverUrl, String clientId, String clientSecret, String username,
			String password) {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Oauth2Constant.GRANT_TYPE, GrantTypeEnum.PASSWORD.getValue());
		paramMap.put(Oauth2Constant.CLIENT_ID, clientId);
		paramMap.put(Oauth2Constant.CLIENT_SECRET, clientSecret);
		paramMap.put(Oauth2Constant.USERNAME, username);
		paramMap.put(Oauth2Constant.PASSWORD, password);
		return getHttpAccessToken(serverUrl + Oauth2Constant.ACCESS_TOKEN_URL, paramMap);
	}

	/**
	 * 获取accessToken（授权码模式）
	 * 
	 * @param serverUrl
	 * @param clientId
	 * @param clientSecret
	 * @param code
	 * @return
	 */
	public static Result<RpcAccessToken> getAccessToken(String serverUrl, String clientId, String clientSecret, String code) {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Oauth2Constant.GRANT_TYPE, GrantTypeEnum.AUTHORIZATION_CODE.getValue());
		paramMap.put(Oauth2Constant.CLIENT_ID, clientId);
		paramMap.put(Oauth2Constant.CLIENT_SECRET, clientSecret);
		paramMap.put(Oauth2Constant.AUTH_CODE, code);
		return getHttpAccessToken(serverUrl + Oauth2Constant.ACCESS_TOKEN_URL, paramMap);
	}

	/**
	 * 刷新accessToken
	 * 
	 * @param serverUrl
	 * @param clientId
	 * @param refreshToken
	 * @return
	 */
	public static Result<RpcAccessToken> refreshToken(String serverUrl, String clientId, String refreshToken) {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put(Oauth2Constant.CLIENT_ID, clientId);
		paramMap.put(Oauth2Constant.REFRESH_TOKEN, refreshToken);
		return getHttpAccessToken(serverUrl + Oauth2Constant.REFRESH_TOKEN_URL, paramMap);
	}

	private static Result<RpcAccessToken> getHttpAccessToken(String url, Map<String, String> paramMap) {
		String jsonStr = HttpUtils.get(url, paramMap);
		if (jsonStr == null || jsonStr.isEmpty()) {
			logger.error("getHttpAccessToken exception, return null. url:{}", url);
			return null;
		}
		return JSONObject.parseObject(jsonStr, new TypeReference<Result<RpcAccessToken>>(){});
	}
}