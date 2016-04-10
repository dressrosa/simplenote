<!--吾志的网页风格 http://wuzhi.me  感谢大神的力量-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="icon" type="image/ico" href="/xiaoyu/img/favicon.ico">
<meta name="robots" content="nofollow">
<meta name="robots" content="noarchive">
<title>${article.title}</title>
<link rel="icon" type="image/ico" href="/xiaoyu/img/favicon.ico">
<meta content="记录每个人生活的点点滴滴。" name="description">
<meta http-equiv="Pragma" content="no-cache">
<link href="/wuzhi/common.css" rel="stylesheet" type="text/css">
<script src="/jquery/jquery-1.12.2.min.js" type="text/javascript"></script>
<style type="text/css">

</style>
</head>
<script>
	$(document).ready(function() {
		var item = '${article.id}';
		$.ajax({
			type : 'POST',
			url : '/back/article/changeView',
			data : {
				id : item
			}
		// ,
		/* 	success : function(data) {
				$("#readNum").html(data);
			} */
		});
	});
</script>
<body>
	<div class="header">
		<div class="container">

			<div class="span-10">
				<span><a class="header_w" href="/xiaoyu/xiaoyu.me.html">首页</a></span>
			</div>

			<div class="prepend-10 span-4 last" style="text-align: right;">
				<a class="header_w" style="padding-right: 15px;"
					href="#">注册</a> <a class="header_w"
					style="padding-right: 0px;" href="/html/app/webLogin.html">登录</a>
			</div>
		</div>
	</div>

	<meta name="robots" content="none">

	<div class="container main">
		<div class="siderbar_left">
			<div class="img_shadow" style="padding-top: 4px;">
				<img src="${article.user.img}" alt="${article.user.nickName}"
					width="300px">
			</div>
			<div class="quote">
				<span>${article.title}</span>
			</div>
		</div>
		<div class="main_right">
			<div
				style="color: #909090; margin-bottom: 15px; border-bottom: 2px #8FA5AB solid; height: 22px;">
				<span style="font-size: 16px; line-height: 20px;"><fmt:formatDate value="${article.createDate}"  pattern="yyyy-MM-dd"/></span>
				<span style="font-size: 16px; line-height: 20px;">浏览量:<strong id="readNum">${article.readNum}</strong></span>
			</div>
			<div class="note_each">
				<!-- <div class="note_time">16:42</div> -->
				<div class="note_content">${article.content}</div>
			</div>
			<div class="note_each">
				<div class="note_time"><fmt:formatDate value="${article.createDate}"  pattern="HH:mm:ss"/></div>
				
				<div class="note_content">${article.user.description }</div>
			</div>
		</div>
		<div class="note_username">-- ${article.user.nickName}</div>
	</div>
<div class="footer container">
 <span class="span-13" onclick="showContent('${article.id}')" id="commentArea">查看评论</span>
</div>
	<div class="footer container">
		<hr class="hr-10">
		<div class="copyright span-5 append-9">
			©2016-20xx 小雨 <a href="#">xiaoyu.me</a>
		</div>
		<div class="span-2 last">
			<a href="#">关于ME</a>
		</div>
	</div>

</body>
<script src="/jquery/jquery-1.12.2.min.js" type="text/javascript"></script>
<script>
function showContent(item) {
	alert(item);
};
</script>
</html>