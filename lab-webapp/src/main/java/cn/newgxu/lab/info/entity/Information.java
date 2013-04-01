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
	@NamedQuery(name = "Information.has_new", query = "SELECT COUNT(*) FROM Information i WHERE i.id > :id"),
	@NamedQuery(name = "Information.list", query = "FROM Information i ORDER BY i.id DESC"),
	@NamedQuery(name = "Information.list_new_count", query = "FROM Information i WHERE i.id > :id ORDER BY i.id DESC"),
	@NamedQuery(name = "Information.list_old", query = "FROM Information i WHERE i.id < :id ORDER BY i.id DESC"),
	@NamedQuery(name = "Information.list_user", query = "FROM Information i WHERE i.user = :user AND i.blocked is FALSE ORDER BY i.id DESC")
})
public class Information {

	@Id
	@GeneratedValue
	private long			id;
	private String			title;
	@Column(length = 10000)
	private String			content;
	@Column(name = "click_times")
	private long			clickTimes;
	@Column(name = "add_date")
	private Date			addDate;
	@Column(name = "last_modified_date")
	private Date			lastModifiedDate;

	/** 是否被屏蔽 */
	private boolean			blocked;

	@ManyToOne(fetch = FetchType.EAGER)
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
