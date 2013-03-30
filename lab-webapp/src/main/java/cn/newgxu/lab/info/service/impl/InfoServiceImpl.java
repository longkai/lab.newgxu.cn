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
package cn.newgxu.lab.info.service.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.newgxu.lab.core.util.Assert;
import cn.newgxu.lab.info.config.Config;
import cn.newgxu.lab.info.entity.Information;
import cn.newgxu.lab.info.repository.InfoDao;
import cn.newgxu.lab.info.service.InfoService;

/**
 * 信息发布对外服务接口的实现。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-29
 * @version 0.1
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Throwable.class)
public class InfoServiceImpl implements InfoService {

	private static final Logger L = LoggerFactory.getLogger(InfoServiceImpl.class);
	
	@Inject
	private InfoDao infoDao;
	
	/** 简单地验证信息是否合法 */
	private void validate(Information info) {
		Assert.notNull("认证用户不能为空！", info.getUser());
		Assert.notEmpty("信息标题不能为空！", info.getTitle());
		Assert.notEmpty("信息内容不能为空！", info.getContent());
	}
	
	@Override
	public void create(Information info) {
		validate(info);
		
		info.setAddDate(new Date());
		info.setLastModifiedDate(info.getAddDate());
		infoDao.persist(info);
		L.info("用户：{} 发表信息：{} 成功！", info.getUser().getAuthorizedName(), info.getTitle());
	}

	@Override
	public void delete(Information info) {
		throw new UnsupportedOperationException("对不起，暂不支持此操作！");
	}

	@Override
	public Information update(Information info) {
		validate(info);
		
		Information i = infoDao.find(info.getId());
		
//		检测信息的作者是否为同一人
		if (!i.getUser().equals(info.getUser())) {
			throw new RuntimeException("对不起，这条信息不是您发布的，您无权修改它！");
		}
		
		i.setLastModifiedDate(new Date());
		i.setTitle(info.getTitle());
		i.setContent(info.getContent());
//		TODO: 如何更优雅地处理文件的上传管理（crud）
		i.setDocUrls(info.getDocUrls());
		
		infoDao.merge(i);
		L.info("信息：{} 修改成功！", info.getTitle());
		return i;
	}

	@Override
	public Information find(long pk) {
		return infoDao.find(pk);
	}
	
	@Override
	public Information view(long pk) {
		Information info = infoDao.find(pk);
		info.setClickTimes(info.getClickTimes() + 1);
		infoDao.merge(info);
		return info;
	}

	@Override
	public long total() {
		return infoDao.size();
	}

	@Override
	public List<Information> list(int NO, int howMany) {
		if (howMany <= 0) {
			howMany = Config.DEFAULT_INFO_LIST_COUNT;
		}
		return infoDao.list(NO, howMany);
	}

	@Override
	public Information block(Information info) {
		validate(info);
		
		Information i = infoDao.find(info.getId());
		i.setBlocked(true);
		
		infoDao.merge(i);
		
		L.info("信息：{} 屏蔽成功！", i.getTitle());
		return i;
	}

	@Override
	public int newerCount(long pk) {
		return infoDao.newerCount(pk);
	}

}