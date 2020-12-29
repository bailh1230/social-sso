package com.smart.sso.server.mapper;

import com.smart.sso.server.model.SsoApp;
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
public interface AppMapper extends Mapper<SsoApp> {
    // Example exampleStudent = new Example(UserInfo.class);
    //     exampleStudent.createCriteria().andEqualTo("courseId", CourseId);
    // List<UserInfo> listStudent=userInfoMapper.selectByExample(exampleStudent);
    List<SsoApp> queryAppByClientId(String clientId);

    List<SsoApp> queryAppByClientIdAndClientSecret(String clientId, String clientSecret);
}

