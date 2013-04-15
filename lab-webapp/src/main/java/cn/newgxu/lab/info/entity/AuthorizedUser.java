/*
 * Copyright (c) 2001-2013 newgxu.cn <the original author or authors>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import cn.newgxu.lab.core.common.JsonDateSerializer;
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
@Table(name = "info_users")
@NamedQueries({
	@NamedQuery(name = "AuthorizedUser.login", query = "FROM AuthorizedUser au WHERE au.account = :account AND au.password = :password"),
	@NamedQuery(name = "AuthorizedUser.account", query = "FROM AuthorizedUser au WHERE au.account = :account"),
	@NamedQuery(name = "AuthorizedUser.list_latest", query = "FROM AuthorizedUser au ORDER BY au.id DESC"),
	@NamedQuery(name = "AuthorizedUser.list_more", query = "FROM AuthorizedUser au WHERE au.id < :last_id ORDER BY au.id DESC"),
	@NamedQuery(name = "AuthorizedUser.list_blocked", query = "FROM AuthorizedUser au WHERE au.blocked IS TRUE ORDER BY au.id DESC")
})
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class AuthorizedUser {

	@Id
	@GeneratedValue
	private long		id;

	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private AccountType	type	= AccountType.DEFAULT;

	@Column(name = "authed_name")
	@JsonProperty("authed_name")
	private String		authorizedName;

	/** 组织，单位，或者个人 */
	private String		org;
	/** 简要信息（可选） */
	private String		about;
	/** 联系方式（可选） */
	private String		contact;

	@Column(name = "join_time")
	@JsonProperty("join_time")
	@JsonSerialize(using = JsonDateSerializer.class)
	private Date		joinTime;

	/** 账号是否被封 */
	@JsonIgnore
	private boolean		blocked;

	@JsonIgnore
	private String		account;
	@JsonIgnore
	private String		password;

	@Column(name = "last_login_ip")
	@JsonIgnore
	private String		lastLoginIP;
	
	@Column(name = "last_login_time")
	@JsonIgnore
//	@JsonSerialize(using = JsonDateSerializer.class)
	private Date		lastLoginTime;

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

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
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

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
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
