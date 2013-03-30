<#-- html头部的定义，这里嵌套了内联得到css -->
<#macro head author="龙凯 longkai @爱因斯坦的狗" email="im.longkai@gmail.com" title="" keywords="" description="" bootstrap="2.3.1">
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>${title}</title>
<meta name="keywords" content="${keywords} 雨无声实验室 广西大学 广西大学雨无声网站 ${author} ${email}" />
<meta name="description" content="${description} 雨无声实验室 广西大学 广西大学雨无声网站 ${author} ${email}" />
<meta name="author" content="${author}" />
<meta name="email" content="${email}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="/resources/libs/bootstrap/${bootstrap}/css/bootstrap.min.css" media="screen" />
<link rel="stylesheet" href="/resources/libs/bootstrap/${bootstrap}/css/bootstrap-responsive.min.css" media="screen" />
<#nested />
</head>
</#macro>


<#-- html主体的定义(不推荐使用) -->
<#macro body>
<body>
<#nested />
</body>
</#macro>

<#-- JavaScript的编写和引入请放在此处 -->
<#macro script jquery="1.9.1" bootstrap="2.3.1">
<script src="//ajax.googleapis.com/ajax/libs/jquery/${jquery}/jquery.min.js"></script>
<script type="text/javascript">
!window.jQuery && document.write('<script src="/resources/js/libs/jquery/${jquery}/jquery.min.js" type="text/javascript"><\/script>');
</script>
<script src="/resources/libs/bootstrap/${bootstrap}/js/bootstrap.min.js"></script>
<#nested />
</html>
</#macro>
