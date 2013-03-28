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
	public long total() {
		return infoDao.size();
	}

	@Override
	public List<Information> list(int NO, int howMany) {
		return infoDao.list(NO, Config.DEFAULT_INFO_LIST_COUNT);
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
	public boolean isLatest(long pk) {
		return infoDao.hasNew(pk);
	}

}
