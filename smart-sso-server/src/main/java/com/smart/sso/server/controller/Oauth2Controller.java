package com.smart.sso.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.sso.client.constant.Oauth2Constant;
import com.smart.sso.client.enums.GrantTypeEnum;
import com.smart.sso.client.rpc.Result;
import com.smart.sso.client.rpc.RpcAccessToken;
import com.smart.sso.client.rpc.SsoUser;
import com.smart.sso.server.common.AuthContent;
import com.smart.sso.server.common.RefreshTokenContent;
import com.smart.sso.server.dto.AuthDto;
import com.smart.sso.server.enums.ClientTypeEnum;
import com.smart.sso.server.service.AppService;
import com.smart.sso.server.service.UserService;
import com.smart.sso.server.session.AccessTokenManager;
import com.smart.sso.server.session.CodeManager;
import com.smart.sso.server.session.RefreshTokenManager;
import com.smart.sso.server.session.TicketGrantingTicketManager;

import java.util.UUID;

/**
 * Oauth2服务管理
 * 
 * @author Joe
 */
@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {
	
	@Autowired
	private AppService appService;
	@Autowired
	private UserService userService;

	@Autowired
	private CodeManager codeManager;
	@Autowired
	private AccessTokenManager accessTokenManager;
	@Autowired
	private RefreshTokenManager refreshTokenManager;
	@Autowired
	private TicketGrantingTicketManager ticketGrantingTicketManager;
	
	/**
	 * 获取accessToken
	 * 
	 * @param clientId
	 * @param clientSecret
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/access_token", method = RequestMethod.GET)
	public Result getAccessToken(
			@RequestParam(value = Oauth2Constant.GRANT_TYPE, required = true) String grantType,
			@RequestParam(value = Oauth2Constant.CLIENT_ID, required = true) String clientId,
			@RequestParam(value = Oauth2Constant.CLIENT_SECRET, required = true) String clientSecret,
			@RequestParam(value = Oauth2Constant.AUTH_CODE, required = false) String code,
			@RequestParam(value = Oauth2Constant.USERNAME, required = false) String username,
			@RequestParam(value = Oauth2Constant.PASSWORD, required = false) String password) {
		
		// 校验基本参数
		Result<Void> result = validateParam(grantType, code, username, password);
		if (!result.isSuccess()) {
			return result;
		}

		// 校验应用
		Result<Void> appResult = appService.validate(clientId, clientSecret);
		if (!appResult.isSuccess()) {
			return appResult;
		}

		// 校验授权
		Result<AuthDto> authResult = validateAuth(grantType, code, username, password);
		if (!authResult.isSuccess()) {
			return authResult;
		}
		AuthDto authDto = authResult.getData();
		
		// 生成RpcAccessToken返回
		return Result.createSuccess(generateRpcAccessToken(authDto.getAuthContent(), authDto.getUser(), clientId, null));
	}
	
	private Result<Void> validateParam(String grantType, String code, String username, String password) {
		if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
			if (StringUtils.isEmpty(code)) {
				return Result.createError("code不能为空");
			}
		}
		else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
			if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
				return Result.createError("username和password不能为空");
			}
		}
		else {
			return Result.createError("授权方式不支持");
		}
		return Result.success();
	}
	
	private Result<AuthDto> validateAuth(String grantType, String code, String username, String password) {
		AuthDto authDto = null;
		if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
			AuthContent authContent = codeManager.validate(code);
			if (authContent == null) {
				return Result.createError("code有误或已过期");
			}

			SsoUser user = ticketGrantingTicketManager.refresh(authContent.getTgt());
			if (user == null) {
				return Result.createError("服务端session已过期");
			}
			authDto = new AuthDto(authContent, user);
		}
		else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
			// app通过此方式由客户端代理转发http请求到服务端获取accessToken
			Result<SsoUser> loginResult = userService.login(username, password);
			if (!loginResult.isSuccess()) {
				return Result.createError(loginResult.getMessage());
			}
			SsoUser user = loginResult.getData();
			String tgt = ticketGrantingTicketManager.generate(loginResult.getData());
			AuthContent authContent = new AuthContent(tgt, ClientTypeEnum.APP, null);
			
			authDto = new AuthDto(authContent, user);
		}
		return Result.createSuccess(authDto);
	}
	
	/**
	 * 刷新accessToken，并延长TGT超时时间
	 * 
	 * accessToken刷新结果有两种：
	 * 1. 若accessToken已超时，那么进行refreshToken会生成一个新的accessToken，新的超时时间；
	 * 2. 若accessToken未超时，那么进行refreshToken不会改变accessToken，但超时时间会刷新，相当于续期accessToken。
	 * 
	 * @param clientId
	 * @param refreshToken
	 * @return
	 */
	@RequestMapping(value = "/refresh_token", method = RequestMethod.GET)
	public Result refreshToken(
			@RequestParam(value = Oauth2Constant.CLIENT_ID, required = true) String clientId,
			@RequestParam(value = Oauth2Constant.REFRESH_TOKEN, required = true) String refreshToken) {
		if(!appService.exists(clientId)) {
			return Result.createError("非法应用");
		}
		
		RefreshTokenContent refreshTokenContent = refreshTokenManager.validate(refreshToken);
		if (refreshTokenContent == null || !clientId.equals(refreshTokenContent.getClientId())) {
			return Result.createError("refreshToken有误或已过期");
		}

		SsoUser user = ticketGrantingTicketManager.refresh(refreshTokenContent.getTgt());
		if (user == null) {
			return Result.createError("服务端session已过期");
		}

		return Result.createSuccess(
				generateRpcAccessToken(refreshTokenContent, user, clientId, refreshTokenContent.getAccessToken()));
	}

	@RequestMapping(value = "/check_token", method = RequestMethod.GET)
	public Result checkToken(
			@RequestParam(value = Oauth2Constant.ACCESS_TOKEN, required = true) String accessToken) {

		RefreshTokenContent accessTokenContent = accessTokenManager.getToken(accessToken);
		if (accessTokenContent == null) {
			return Result.createError("accessToken有误或已过期");
		}

		SsoUser user = ticketGrantingTicketManager.refresh(accessTokenContent.getTgt());
		if (user == null) {
			return Result.createError("服务端session已过期");
		}

		return Result.createSuccess(new RpcAccessToken(accessToken, accessTokenManager.getExpiresIn(), accessTokenContent.getAccessToken(), user));
	}
	
	private RpcAccessToken generateRpcAccessToken(AuthContent authContent, SsoUser user, String clientId,
												  String accessToken) {
		String newAccessToken = accessToken;
		if (newAccessToken != null) {
			RefreshTokenContent refreshTokenContent = accessTokenManager.getToken(newAccessToken);
			if (refreshTokenContent != null) {
				String tgt = refreshTokenContent.getTgt();
				accessTokenManager.remove(tgt);
			}
		}
		newAccessToken = "AT-" + UUID.randomUUID().toString().replaceAll("-", "");

		String refreshToken = refreshTokenManager.generate(authContent, newAccessToken, clientId);
		RefreshTokenContent refreshTokenContent = new RefreshTokenContent(authContent.getTgt(), authContent.getClientType(),
				authContent.getRedirectUri(), refreshToken, clientId);
		accessTokenManager.generate(newAccessToken, refreshTokenContent);

		return new RpcAccessToken(newAccessToken, accessTokenManager.getExpiresIn(), refreshToken, user);
	}
}