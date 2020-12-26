package com.smart.sso.server.model;

import com.smart.sso.server.util.UuidGenId;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 应用
 */
@Data
@Table(name = "sso_app")
public class SsoApp implements Serializable {

	private static final long serialVersionUID = 14358427303197385L;

	/** id */
	@Id
	@KeySql(genId = UuidGenId.class)
	@Column(name = "id")
	private String id;

	/** 名称 */
	@Column(name = "client_name")
	private String clientName;
	/** 应用唯一标识 */
	@Column(name = "client_id")
	private String clientId;
	/** 应用密钥 */
	@Column(name = "client_secret")
	private String clientSecret;

	@Column(name = "redirect_uri")
	private String redirectUri;

	@Column(name = "create_time")
	private Date createTime;

	public SsoApp() {
		super();
	}

	public SsoApp(String id, String clientName, String clientId, String clientSecret) {
		super();
		this.id = id;
		this.clientName = clientName;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
}
