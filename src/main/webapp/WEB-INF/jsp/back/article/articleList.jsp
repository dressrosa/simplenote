<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/head.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/wuzhi/common.css" rel="stylesheet" type="text/css">
<link href="/stephanwagner/9aad28-424d89.css" rel="stylesheet">
<script src="/jqueryflip/jquery-ui.min.js" type="text/javascript"></script>
<script src="/jqueryflip/jquery.flip.min.js" type="text/javascript"></script>
<script src="/jqueryflip/script.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="/jqueryflip/flip.css" />
<style>
</style>
<script>
	$(function() {
		$(".col-xs-3").sortable({
			connectWith : '.col-xs-3',//级联其他
			containment: "document",//作用范围
			delay: 150,//延迟效果
			revert: true //动画
			
		});
		$(".col-xs-3").disableSelection();
	});
</script>
<title>文章列表</title>
</head>
<body>

	<div class="container-fluid">
		<div class="header">
			<div class="container">

				<div class="span-10">
					<span><a class="header_w" href="/xiaoyu/xiaoyu.me.html">首页</a></span>

				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="span12">
				<div class="row-fluid">
					<c:forEach begin="0" end="4" var="child" items="${list}"
						varStatus="status">
						<div class="col-xs-3 span3"
							style="font-family: cursive; font-size: medium;top: 15px;">
							<c:forEach begin="0" end="3" var="article" items="${child}">
								<dl>
									<div class="sponsor" title="点心醒梦">
										<div class="sponsorFlip" id="${article.id}">
											<dt>${article.title}</dt>
											<p>${article.content}</p>
											<div class="g-line" style="width: 25%; margin: 25px auto;"></div>

										</div>
										<div class="sponsorData" id="${article.user.id }">
											<dt>${article.user.nickName}</dt>
											<span><img src="${article.user.img}"
												class="img-circle" height="160px" width="160px" /></span>
											<p>${article.user.description }</p>
											<div class="g-line" style="width: 25%; margin: 25px auto;"></div>
										</div>
									</div>
								</dl>
							</c:forEach>

						</div>

					</c:forEach>

				</div>
			</div>
		</div>
		<div id="footer">
			©<a href="">xiao yu</a>
		</div>
	</div>
</body>


</html>