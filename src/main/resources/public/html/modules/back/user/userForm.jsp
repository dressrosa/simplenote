<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/head.jsp"%>
<title>${user.nickName}</title>
</head>
<body>
	<%@ include file="/WEB-INF/jsp/common/container"%>
	<div class="container">
		<div class="row">
			<form id="xyForm" role="form" modelAttribute="user" action="save"
				method="post">
				<input type="hidden" value="${user.id}" id="id" name="id">
				<fieldset>
					<div class="span12">
						<div class="panel panel-success">
							<div class="panel-heading">
								<h3 class="panel-title">信息修改</h3>
							</div>
							<div class="panel-body">
								<label>姓名</label> <input value="${user.nickName}" type="text"
									class="form-control" id="name" name="name" placeholder="请输入姓名">
								<!-- <span class="help-block">这里填写帮助信息.</span> -->
							</div>
							<div class="panel-body">
								<label>签名</label> <input value="${user.description}" type="text"
									class="form-control" id="description" name="description" >
							</div>
							<div class="panel-body">
							<label>头像</label><br>
							<input type="hidden" value="${user.img}" name="img"   />
								<img src="${user.img}" id="img"
									class="img-circle" width="100px;" height="100px"
									onclick="uploadFile()">
							</div>
							<div class="panel-footer">
								<button type=button class="btn btn-large btn-warning"
									onclick="update('update')" data-confirm="确认修改信息么">提交</button>
							</div>
						</div>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
	<%@ include file="/WEB-INF/jsp/common/footer"%>
</body>
</html>
