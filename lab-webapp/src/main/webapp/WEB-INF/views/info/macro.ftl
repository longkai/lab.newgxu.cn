<#-- 顶部的导航栏 -->
<#macro header>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a href="#" class="brand">信息发布平台</a>
			<div class="nav-collapse collapse">
				<ul class="nav">
					<li class="active" id="_index"><a href="/info/">首页</a></li>
					<li><a href="/resources/html/info/about.html">关于</a></li>
					<li><a href="/resources/html/info/auth.html">认证</a></li>
					<li><a href="#">服务条款</a></li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">雨无声实验室 <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="/">首页</a></li>
							<li><a href="/about.html">关于实验室</a></li>
						</ul>
					</li>
				</ul>
<#if Session.info_user??>
				<div class="btn-group pull-right">
					<a class="btn btn-primary" href="#"><i class="icon-user icon-white"></i> ${Session.info_user.authorizedName}</a>
					<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a href="/resources/html/info/new_info.html"><i class="icon-pencil"></i> 发表信息</a></li>
						<li><a href="/info/user/update/${Session.info_user.id}"><i class="icon-edit"></i> 修改个人信息</a></li>
						<li><a href="/info/info/list/user/${Session.info_user.id}"><i class="icon-th-list"></i> 查看我发表的信息</a></li>
						<li class="divider"></li>
						<li id="logout"><a href="/info/logout"><i class="i"></i> 退出</a></li>
					</ul>
				</div>
<#else>				
				<form action="#" class="navbar-form pull-right" method="post" id="login_form">
					<input type="text" class="span2" name="account" placeholder="账号" required />
					<input type="password" class="span2" name="pwd" placeholder="密码" required />
					<button type="button" class="btn">登陆</button>
				</form>
</#if>		
			</div>
		</div>
	</div>
</div>
</#macro>

<#-- 查看文章 -->
<#macro view_info>
<div class="jumbotron">
	<div class="alert alert-error" id="alert">
		<p class="text-center"><span id="status"></span><span id="info"></span> <span id="reason"></span></p>
	</div>
	<h1>${info.title}</h1>
	<p class="pull-right">
		<span id="author"><code><a href="javascript:userDetail(${info.user.id})">${info.user.authorizedName}</a></code></span> <span class="time"><code>[${info.addDate?string('yyyy-MM-dd HH:mm')}]</code></span> <span><code class="text-info">${info.clickTimes}点击</code></span>
<#if Session.info_user??>
	<#if Session.info_user.id == info.user.id>
		<code><a href="/info/info/modify?id=${info.id}" class="text-warning">修改我发表的信息</a></code>
	</#if>
</#if>
	</p>
	<p id="content"></p>
<#if info.docUrl??>	
	<hr />
	<p id="file">
		<h4>附件下载</h4>
		<a href="${info.docUrl}">${info.docName?default('下载')}</a>
	</p>
</#if>
	<p class="pull-right"><code>最后修改于${info.lastModifiedDate?string('yyyy-MM-dd HH:mm')}</span></code></p>
</div>
</#macro>