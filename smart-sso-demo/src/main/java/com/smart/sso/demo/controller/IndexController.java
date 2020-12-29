package com.smart.sso.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smart.sso.client.model.SessionPermission;
import com.smart.sso.client.model.SessionUser;
import com.smart.sso.client.util.SessionUtils;

@Controller
public class IndexController {

	@Value("${sso.server.url}")
	private String ssoServerUrl;

	@GetMapping("/")
	public String index(Model model, HttpServletRequest request) {

		SessionUser sessionUser = SessionUtils.getUser(request);
		// 登录用户名
		model.addAttribute("userName", sessionUser.getAccount());

		SessionPermission sessionPermission = SessionUtils.getPermission(request);
		if (sessionPermission != null) {
			// 登录用户当前应用的菜单
			request.setAttribute("userMenus", sessionPermission.getMenuList());
			// 登录用户当前应用的权限
			request.setAttribute("userPermissions", sessionPermission.getPermissionSet());
		}
		
		// 单点退出地址
		model.addAttribute("ssologoutUrl", ssoServerUrl + "/logout?service=" + getLocalUrl(request).toString());
		return "index";
	}
	

    /**
     * 获取当前应用访问路径
     *
     * @param request
     * @return
     */
    private String getLocalUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder();
        url.append(request.getScheme()).append("://").append(request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url.append(":").append(request.getServerPort());
        }
        url.append(request.getContextPath());
        return url.toString();
    }
}
