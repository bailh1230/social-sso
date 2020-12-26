package com.smart.sso.client.constant;

/**
 * @author Joe
 */
public class Oauth2Constant {

	/**
	 * 授权
	 */
	public static final String AUTH = "auth";

	/**
	 * 用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止 csrf 攻击
	 */
	public static final String STATE = "state";
	
	/**
	 * 授权方式
	 */
	public static final String GRANT_TYPE = "grant_type";

	/**
	 * 应用名
	 */
	public static final String CLIENT_NAME = "client_name";

	/**
	 * 应用唯一标识
	 */
	public static final String CLIENT_ID = "client_id";
	
	/**
	 * 应用密钥
	 */
	public static final String CLIENT_SECRET = "client_secret";
	
	/**
	 * 刷新token
	 */
	public static final String REFRESH_TOKEN = "refresh_token";

	/**
	 * 刷新token
	 */
	public static final String ACCESS_TOKEN = "access_token";
	
	/**
	 * 授权码（授权码模式）
	 */
	public static final String AUTH_CODE = "code";
	
	/**
	 * 用户名（密码模式）
	 */
	public static final String USERNAME = "username";
	
	/**
	 * 密码（密码模式）
	 */
	public static final String PASSWORD = "password";
	
	/**
	 * 获取accessToken地址
	 */
	public static final String ACCESS_TOKEN_URL = "/oauth2/access_token";
	
	/**
	 * 刷新accessToken地址
	 */
	public static final String REFRESH_TOKEN_URL = "/oauth2/refresh_token";

	/**
	 * 授权地址
	 */
	public static final String AUTH_URL = "/auth";
}
