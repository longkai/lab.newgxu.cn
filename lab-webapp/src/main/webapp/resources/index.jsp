<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8"/>
<title>Insert title here</title>
<style type="text/css">
	#main {
		width: 960px;
		margin: 0 auto;
	}
	form {

	}
</style>
<script>
window.UEDITOR_HOME_URL = "/resources/libs/ueditor/1.2.6.1/";
</script>
<script type="text/javascript" charset="utf-8" src="/resources/libs/ueditor/1.2.6.1/ueditor.config.js"></script>
<script type="text/javascript" charset="utf-8" src="/resources/libs/ueditor/1.2.6.1/ueditor.all.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var editor = new UE.ui.Editor({
			textarea : 'description',
			initialFrameWidth:910,
			initialFrameHeight:400
		});
		editor.render("txtEditor");

		$('#btnSubmit').click(function(e) {
			e.preventDefault();
			$.ajax({
				url: '/post',
				type: 'POST',
				data: {
					content: editor.getContent()
				},
				success: function(data) {
					console.log(data);
				}
			})
		})
	});
</script>
</head>
<body>
	<div id="main">
		<form action="show.jsp" id="upForm" name="upForm" method="post">
			<script type="text/plain" id="txtEditor">${description}</script>
			<input type="submit" value="submit" id="btnSubmit"/>
		</form>
	</div>
	<%=new java.util.Date()%>
</body>
<script type="text/javascript">
	$(function() {

	})
</script>
</html>