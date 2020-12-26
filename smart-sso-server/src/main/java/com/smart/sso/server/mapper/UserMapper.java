package com.smart.sso.server.mapper;

import com.smart.sso.server.model.SsoUser;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/***
* @description
* @author        bailihong
* @createdate    2020/11/29 22:42
* @version       v1.0
*/
@Component
public interface UserMapper extends Mapper<SsoUser> {
    List<SsoUser> queryUserByUsername(String username);
}
