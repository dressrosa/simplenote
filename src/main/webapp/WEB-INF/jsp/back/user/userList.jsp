<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/head.jsp"%>
<title>用户信息</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/common/container"%>
	<div class="content">

		<div class="table-responsive">
			<table class="table">
				<caption>响应式表格布局</caption>
				<span><a href="/html/back/userEcharts.html">Echarts-用户统计分析</a></span>
				<thead>
					<tr>
						<th>姓名</th>
						<th>头像</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${list}" var="user">
						<tr>
							<td><a onclick="getDetail('get',${user.id})">
							${user.name}</td>
							</a>
							<td><a href="${user.img}" title="${user.name}" data-jbox-image="gallery1">
							<img src="${user.img}" id="img" name="img"
								class="img-circle" width="50px;" height="50px">
							</a>
							</td>
							<td><a href="goUpdate?id=${user.id}">修改</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<%@ include file="/WEB-INF/jsp/common/footer"%>
</body>

</html>
<script type="text/javascript">
new jBox('Image');
</script>