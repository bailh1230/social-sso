package com.smart.sso.server.service.impl;

import java.util.List;

import com.smart.sso.server.mapper.AppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.sso.client.rpc.Result;
import com.smart.sso.server.model.SsoApp;
import com.smart.sso.server.service.AppService;

@Service("appService")
public class AppServiceImpl implements AppService {
	@Autowired
	AppMapper appMapper;

	@Override
	public boolean exists(String clientId) {
		SsoApp app = new SsoApp();
		app.setClientId(clientId);
		List<SsoApp> appList = appMapper.select(app);
		if (appList != null && appList.size() > 0) {
			return true;
		} else {
			return false;
		}
		// return appList.stream().anyMatch(app -> app.getClientId().equals(clientId));
	}

	@Override
	public Result<Void> validate(String clientId, String clientSecret) {
		SsoApp app = new SsoApp();
		app.setClientId(clientId);
		app.setClientSecret(clientSecret);
		List<SsoApp> appList = appMapper.select(app);
		if (appList != null && appList.size() > 0) {
			return Result.success();
		} else {
			return Result.createError("clientSecret有误");
		}
		// for (App app : appList) {
		// 	if (app.getClientId().equals(clientId)) {
		// 		if (app.getClientSecret().equals(clientSecret)) {
		// 			return Result.success();
		// 		}
		// 		else {
		// 			return Result.createError("clientSecret有误");
		// 		}
		// 	}
		// }
		// return Result.createError("clientId不存在");
	}

	@Override
	public SsoApp getApp(String clientId) {
		SsoApp app = new SsoApp();
		app.setClientId(clientId);
		return appMapper.selectOne(app);
	}
}
