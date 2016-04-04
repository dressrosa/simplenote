<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/head.jsp"%>
<title>${user.name}</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/common/container"%>
	<div class="content">
<div class="panel panel-default">
   <div class="panel-heading">${user.name}</div>
      <div class="panel-body">
		<img alt="" src="${user.img}" class="img-circle" height="100px" width="100px">
   </div>
   <ul class="list-group">
      <li class="list-group-item"></li>
   </ul>
</div>
	</div>
	<%@ include file="/WEB-INF/jsp/common/footer"%>
</body>

</html>