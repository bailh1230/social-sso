package com.smart.sso.server.controller;

import com.smart.sso.client.constant.Oauth2Constant;
import com.smart.sso.client.constant.SsoConstant;
import com.smart.sso.client.rpc.Result;
import com.smart.sso.client.rpc.SsoUser;
import com.smart.sso.server.constant.AppConstant;
import com.smart.sso.server.enums.ClientTypeEnum;
import com.smart.sso.server.model.SsoApp;
import com.smart.sso.server.service.AppService;
import com.smart.sso.server.service.UserService;
import com.smart.sso.server.session.CodeManager;
import com.smart.sso.server.session.TicketGrantingTicketManager;
import com.smart.sso.server.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 单点登录管理
 * 
 * @author Joe
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private CodeManager codeManager;
	@Autowired
	private TicketGrantingTicketManager ticketGrantingTicketManager;
	@Autowired
	private UserService userService;
	@Autowired
	private AppService appService;

	/**
	 * 登录页
	 *
	 * @param redirectUri
	 * @param clientId
	 * @param request
	 * @return
	 */
	// @RequestMapping(method = RequestMethod.GET)
	// public String auth(
	// 		@RequestParam(value = SsoConstant.REDIRECT_URI, required = false) String redirectUri,
	// 		@RequestParam(value = Oauth2Constant.CLIENT_ID, required = false) String clientId,
	// 		HttpServletRequest request) throws UnsupportedEncodingException {
	// 	SsoApp ssoApp = appService.getApp(clientId);
	// 	request.setAttribute(SsoConstant.REDIRECT_URI, redirectUri);
	// 	request.setAttribute(Oauth2Constant.CLIENT_ID, clientId);
	// 	request.setAttribute(Oauth2Constant.CLIENT_NAME, ssoApp.getClientName());
	// 	return Oauth2Constant.AUTH_URL;
	// }

	/**
	 * 登录提交
	 * 
	 * @param redirectUri
	 * @param auth
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String auth(
			@RequestParam(value = SsoConstant.REDIRECT_URI, required = true) String redirectUri,
			@RequestParam(value = Oauth2Constant.AUTH, required = false) String[] auth,
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		String tgt = CookieUtils.getCookie(request, AppConstant.TGC);
		if (auth != null) {
			return generateCodeAndRedirect(redirectUri, tgt);
		} else {
			String logoutUrl = new StringBuilder().append(SsoConstant.LOGOUT_URL).append("?")
					.append(SsoConstant.REDIRECT_URI).append("=")
					.append(URLEncoder.encode(redirectUri, "utf-8")).toString();
			return "redirect:" + logoutUrl;
		}

	}
	
	/**
	 * 生成授权码，跳转到redirectUri
	 * 
	 * @param redirectUri
	 * @param tgt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String generateCodeAndRedirect(String redirectUri, String tgt) throws UnsupportedEncodingException {
		// 生成授权码
		String code = codeManager.generate(tgt, ClientTypeEnum.WEB, redirectUri);
		return "redirect:" + authRedirectUri(redirectUri, code);
	}

	/**
	 * 将授权码拼接到回调redirectUri中
	 * 
	 * @param redirectUri
	 * @param code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String authRedirectUri(String redirectUri, String code) throws UnsupportedEncodingException {
		StringBuilder sbf = new StringBuilder(redirectUri);
		if (redirectUri.indexOf("?") > -1) {
			sbf.append("&");
		}
		else {
			sbf.append("?");
		}
		sbf.append(Oauth2Constant.AUTH_CODE).append("=").append(code);
		return URLDecoder.decode(sbf.toString(), "utf-8");
	}

}