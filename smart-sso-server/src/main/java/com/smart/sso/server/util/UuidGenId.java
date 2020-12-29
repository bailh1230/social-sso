package com.smart.sso.server.util;

import tk.mybatis.mapper.genid.GenId;

import java.util.UUID;

/**
 * @ClassName com.techsun.cdp.recommendercenter.utils.UuidGenId
 * @Description:
 * @Author: bailihong
 * @CreateDate: 2019/6/11 16:45
 * @Version: 1.0
 */

public class UuidGenId implements GenId<String> {
    @Override
    public String genId(String s, String s1) {
//        return UUID.randomUUID().toString().replace("-","");
        return UUID.randomUUID().toString();
    }
}
