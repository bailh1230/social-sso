package com.smart.sso.server.session;

import java.util.UUID;

import com.smart.sso.client.rpc.SsoUser;
import com.smart.sso.server.common.Expiration;

/**
 * 登录凭证（TGT）管理抽象
 * 
 * @author Joe
 */
public interface TicketGrantingTicketManager extends Expiration {
	
    /**
     * 登录成功后，根据用户信息生成令牌
     * 
     * @param user
     * @return
     */
	default String generate(SsoUser user) {
		String tgt = "TGT-" + UUID.randomUUID().toString().replaceAll("-", "");
		create(tgt, user);
		return tgt;
	}
    
    /**
     * 登录成功后，根据用户信息生成令牌
     * 
     * @param user
     * @return
     */
    void create(String tgt, SsoUser user);
    
    /**
     * 验证st是否存在，且在有效期内
     * 
     * @param tgt
     * @return
     */
    SsoUser get(String tgt);
    
    /**
     * 移除
     * 
     * @param tgt
     */
    void remove(String tgt);
    
    /**
     * 存在，则延长TGT生命周期，返回true。不存在，返回false
     * 
     * @param tgt
     * @return
     */
    SsoUser refresh(String tgt);
}
