package com.smart.sso.server.service.impl;

import java.util.List;

import com.smart.sso.server.mapper.UserMapper;
import com.smart.sso.server.model.SsoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.sso.client.rpc.Result;
import com.smart.sso.server.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	private static List<SsoUser> ssoUserList;

	@Autowired
	UserMapper userMapper;
	
	@Override
	public Result<com.smart.sso.client.rpc.SsoUser> login(String username, String password) {
		SsoUser example = new SsoUser();
		example.setUsername(username);
		ssoUserList = userMapper.select(example);
		for (SsoUser ssoUser : ssoUserList) {
			if (ssoUser.getUsername().equals(username)) {
				if(ssoUser.getPassword().equals(password)) {
					return Result.createSuccess(new com.smart.sso.client.rpc.SsoUser(ssoUser.getId(), ssoUser.getUsername()));
				}
				else {
					return Result.createError("密码有误");
				}
			}
		}
		return Result.createError("用户不存在");
	}
}
