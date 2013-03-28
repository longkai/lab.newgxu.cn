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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 信息。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
@Entity
@Table(name = "info_info")
@NamedQueries({ 
	@NamedQuery(name = "Information.user", query = "FROM Information i WHERE i.user = :user"),
	@NamedQuery(name = "Information.has_new", query = "SELECT COUNT(*) FROM Information i WHERE i.id > :id")
})
public class Information {

	@Id
	@GeneratedValue
	private long			id;
	private String			title;
	private String			content;
	@Column(name = "click_times")
	private long			clickTimes;
	@Column(name = "add_date")
	private Date			addDate;
	@Column(name = "last_modified_date")
	private Date			lastModifiedDate;

	/** 是否被屏蔽 */
	private boolean			blocked;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "auth_user_id")
	private AuthorizedUser	user;

	/** 上传文档的存放路径，采用分隔符来分隔多个文档 */
	@Column(name = "doc_urls")
	private String			docUrls;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getClickTimes() {
		return clickTimes;
	}

	public void setClickTimes(long clickTimes) {
		this.clickTimes = clickTimes;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public AuthorizedUser getUser() {
		return user;
	}

	public void setUser(AuthorizedUser user) {
		this.user = user;
	}

	public String getDocUrls() {
		return docUrls;
	}

	public void setDocUrls(String docUrls) {
		this.docUrls = docUrls;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Information other = (Information) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Information [id=" + id + ", title=" + title + "]";
	}

}
