package com.itranswarp.wxapi.token;

import org.springframework.stereotype.Component;

import com.itranswarp.wxapi.exception.WeixinAccessTokenException;

@Component
public class SimpleAccessTokenCache implements AccessTokenCache {

	/**
	 * Should refresh in 10 min:
	 */
	static final long SHOULD_REFRESH_IN = 10 * 60 * 1000;

	AccessTokenBean accessTokenBean;

	@Override
	public void setAccessToken(String accessToken, int expiresInSeconds) {
		this.accessTokenBean = new AccessTokenBean(accessToken, System.currentTimeMillis() + expiresInSeconds * 1000L);
	}

	@Override
	public String getAccessToken() {
		AccessTokenBean bean = this.accessTokenBean;
		if (bean == null || System.currentTimeMillis() > bean.expiresAt) {
			throw new WeixinAccessTokenException("Missing access token or already expired.");
		}
		return bean.accessToken;
	}

	@Override
	public boolean shouldRefresh() {
		return System.currentTimeMillis() - this.accessTokenBean.expiresAt < SHOULD_REFRESH_IN;
	}

	static class AccessTokenBean {

		final String accessToken;
		final long expiresAt;

		public AccessTokenBean(String accessToken, long expiresAt) {
			this.accessToken = accessToken;
			this.expiresAt = expiresAt;
		}
	}
}