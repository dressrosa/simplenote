<!--吾志的网页风格 http://wuzhi.me  感谢大神的力量-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<link rel="icon" type="image/ico" href="/xiaoyu/img/favicon.ico">
<meta name="robots" content="nofollow">
<meta name="robots" content="noarchive">
<title>写日志</title>
<link rel="icon" type="image/ico" href="/xiaoyu/img/favicon.ico">
<meta http-equiv="Pragma" content="no-cache">
<link href="/wuzhi/common.css" rel="stylesheet" type="text/css">
<script src="/jquery/jquery-1.12.2.min.js" type="text/javascript"></script>
<script src="/xiaoyu/common.js" type="text/javascript"></script>
<script type="text/javascript" src="/xiaoyu/jquerysession.js"></script>
<style type="text/css">
</style>
</head>
<script type="text/javascript">
//$(document).ready(function() {
//	});
</script>
<body>
	<div class="header">
		<div class="container">
			<div class="span-10">
				<span><a class="header_w" href="/xiaoyu/xiaoyu.me.html">首页</a></span>
			</div>
			<div class="prepend-10 span-4 last" style="text-align: right;">
				<a class="header_w" style="padding-right: 15px;" id="nickName" href="#">${user.nickName}</a>
				<a class="header_w" style="padding-right: 0px;"
					 id="logout" href="/app/user/logout">退出</a>
			</div>
		</div>
	</div>

	<meta name="robots" content="none">

	<div class="container main">
		<div class="main">
			<div class="note_each">
				<div class="note_content">此处写文章</div>
			</div>
		</div>
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

<script type="text/javascript">
	
</script>
</html>