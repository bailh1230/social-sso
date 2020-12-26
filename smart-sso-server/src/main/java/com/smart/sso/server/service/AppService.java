package com.smart.sso.server.service;

import com.smart.sso.client.rpc.Result;
import com.smart.sso.server.model.SsoApp;

/**
 * 应用服务接口
 * 
 * @author Joe
 */
public interface AppService {

	boolean exists(String clientId);
	
	Result<Void> validate(String clientId, String clientSecret);

	SsoApp getApp(String clientId);
}
