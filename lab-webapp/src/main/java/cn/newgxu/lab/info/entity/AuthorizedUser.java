/*
 * Copyright 2001-2013 newgxu.cn the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.newgxu.lab.info.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import cn.newgxu.lab.info.config.AccountType;

/**
 * 认证的用户。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Entity
@Table(name = "info_auth_user")
@NamedQueries({
		@NamedQuery(name = "AuthorizedUser.login", query = "FROM AuthorizedUser au WHERE au.account = :account AND au.password = :password"),
		@NamedQuery(name = "AuthorizedUser.account", query = "FROM AuthorizedUser au WHERE au.account = :account"),
		@NamedQuery(name = "AuthorizedUser.list", query = "FROM AuthorizedUser au")
})
public class AuthorizedUser {

	@Id
	@GeneratedValue
	private long		id;

	@Enumerated(EnumType.STRING)
	private AccountType	type	= AccountType.DEFAULT;

	@Column(name = "auth_name")
	private String		authorizedName;

	/** 组织，单位，或者个人 */
	private String		org;

	@Column(name = "join_time")
	private Date		joinTime;

	/** 账号是否被封 */
	private boolean		blocked;

	private String		account;
	private String		password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public String getAuthorizedName() {
		return authorizedName;
	}

	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public Date getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthorizedUser other = (AuthorizedUser) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AuthorizedUser [id=" + id + ", authorizedName="
				+ authorizedName + "]";
	}

}
