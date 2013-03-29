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
package cn.newgxu.lab.info.service;

import java.util.List;

import cn.newgxu.lab.info.entity.Information;

/**
 * 信息发布平台的服务接口。
 * 
 * @author longkai
 * @email im.longkai@gmail.com
 * @since 2013-3-28
 * @version 0.1
 */
public interface InfoService {

	void create(Information info);

	void delete(Information info);

	Information update(Information info);

	Information find(long pk);
	
	/** 区别于find，这个会增加1的点击率 */
	Information view(long pk);

	long total();

	List<Information> list(int NO, int howMany);

	/**
	 * 屏蔽信息。
	 * @param info 欲屏蔽的信息对象
	 */
	Information block(Information info);

	/**
	 * 是否有更新，判断是否有比传过来的参数更大的主键 
	 * @param pk 客户端上最新的主键
	 * @return true or false
	 */
	int newerCount(long pk);

}
