package com.smart.sso.server.session.local;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.smart.sso.server.common.RefreshTokenContent;
import com.smart.sso.server.common.ExpirationPolicy;
import com.smart.sso.server.enums.ClientTypeEnum;
import com.smart.sso.server.session.AccessTokenManager;

/**
 * 本地调用凭证管理
 * 
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "local")
public class LocalAccessTokenManager implements AccessTokenManager, ExpirationPolicy {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${sso.timeout}")
    private int timeout;

	private Map<String, DummyAccessToken> accessTokenMap = new ConcurrentHashMap<>();
	private Map<String, Set<String>> tgtMap = new ConcurrentHashMap<>();

	@Override
	public void create(String accessToken, RefreshTokenContent refreshTokenContent) {
		long createTime = System.currentTimeMillis();
		refreshTokenContent.setCreateTime(createTime);
		DummyAccessToken dat = new DummyAccessToken(refreshTokenContent, createTime + getExpiresIn() * 1000);
		accessTokenMap.put(accessToken, dat);
		tgtMap.computeIfAbsent(refreshTokenContent.getTgt(), a -> new HashSet<>()).add(accessToken);
	}

	@Override
	public boolean refresh(String accessToken) {
		DummyAccessToken dummyAt = accessTokenMap.get(accessToken);
		if (dummyAt == null || System.currentTimeMillis() > dummyAt.expired) {
			return false;
		}
		dummyAt.expired = System.currentTimeMillis() + getExpiresIn() * 1000;
		return true;
	}

	@Override
	public void remove(String tgt) {
		Set<String> accessTokenSet = tgtMap.remove(tgt);
		if (CollectionUtils.isEmpty(accessTokenSet)) {
			return;
		}
		accessTokenSet.forEach(accessToken -> {
			DummyAccessToken dummyAt = accessTokenMap.get(accessToken);
			if (dummyAt == null || System.currentTimeMillis() > dummyAt.expired) {
				return;
			}
			RefreshTokenContent refreshTokenContent = dummyAt.refreshTokenContent;
			if (refreshTokenContent == null || refreshTokenContent.getClientType() != ClientTypeEnum.WEB) {
				return;
			}
			sendLogoutRequest(refreshTokenContent.getRedirectUri(), accessToken);
			accessTokenMap.remove(accessToken);
		});
	}

	@Scheduled(cron = SCHEDULED_CRON)
	@Override
	public void verifyExpired() {
		accessTokenMap.forEach((accessToken, dummyAt) -> {
			if (System.currentTimeMillis() > dummyAt.expired) {
				accessTokenMap.remove(accessToken);
				logger.debug("accessToken : " + accessToken + "已失效");
			}
		});
	}
	
	/**
	 * accessToken时效为登录session时效的1/2
	 */
	@Override
	public int getExpiresIn() {
		return timeout / 2;
	}

	private class DummyAccessToken {
		private RefreshTokenContent refreshTokenContent;
		private long expired; // 过期时间

		public DummyAccessToken(RefreshTokenContent refreshTokenContent, long expired) {
			super();
			this.refreshTokenContent = refreshTokenContent;
			this.expired = expired;
		}

		public void setRefreshTokenContent(RefreshTokenContent refreshTokenContent) {
			this.refreshTokenContent = refreshTokenContent;
		}
	}

	@Override
	public RefreshTokenContent getToken(String accessToken) {
		DummyAccessToken dummyAt = accessTokenMap.get(accessToken);
		if (dummyAt == null || System.currentTimeMillis() > dummyAt.expired) {
			return null;
		}
		return dummyAt.refreshTokenContent;
	}
}
