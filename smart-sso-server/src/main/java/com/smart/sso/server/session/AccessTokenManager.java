package com.smart.sso.server.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smart.sso.client.constant.SsoConstant;
import com.smart.sso.client.util.HttpUtils;
import com.smart.sso.server.common.RefreshTokenContent;
import com.smart.sso.server.common.AuthContent;
import com.smart.sso.server.common.Expiration;

/**
 * 调用凭证AccessToken管理抽象
 * 
 * @author Joe
 */
public interface AccessTokenManager extends Expiration {

	/**
	 * 生成AccessToken
	 * 
	 * @param refreshTokenContent
	 * @return
	 */
	default String generate(String accessToken, RefreshTokenContent refreshTokenContent) {
		create(accessToken, refreshTokenContent);
		return accessToken;
	}

	/**
	 * 生成AccessToken
	 * 
	 * @param accessToken
	 * @param accessTokenContent
	 */
	void create(String accessToken, RefreshTokenContent accessTokenContent);

	/**
	* 获取访问令牌
	* @createdate     2020/12/25 0:38
	* @param accessToken  accessToken
	* @return         com.smart.sso.server.common.RefreshTokenContent
	*/
	RefreshTokenContent getToken(String accessToken);

	/**
     * 延长AccessToken生命周期
     * 
	 * @param accessToken
	 * @return
	 */
	boolean refresh(String accessToken);
	
	/**
	 * 根据TGT删除AccessToken
	 * 
	 * @param tgt
	 */
	void remove(String tgt);
	
	/**
	 * 发起客户端登出请求
	 * 
	 * @param redirectUri
	 * @param accessToken
	 */
	default void sendLogoutRequest(String redirectUri, String accessToken) {
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put(SsoConstant.LOGOUT_PARAMETER_NAME, accessToken);
		HttpUtils.postHeader(redirectUri, headerMap);
	}
}
