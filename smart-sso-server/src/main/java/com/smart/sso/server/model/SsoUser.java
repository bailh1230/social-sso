package com.smart.sso.server.model;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 */
@Data
@Table(name = "sso_user")
public class SsoUser implements Serializable {

	private static final long serialVersionUID = 10125567610925057L;

	/** ID */
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/** 登录名 */
	@Column(name = "username")
	private String username;
	/** 密码 */
	@Column(name = "password")
	private String password;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;
	
	public SsoUser() {
		super();
	}

	public SsoUser(Integer id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
}
