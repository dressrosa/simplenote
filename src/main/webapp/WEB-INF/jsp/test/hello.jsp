<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=yes">
<title>Session Attributes</title>
<link href="/bootstrap-3.3.6-dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body>
	<div  class="container">
		<h1>Description</h1>
		<p>This application demonstrates
			 how to use a Redis instance to back your session. Notice that there is no JSESSIONID 
			cookie. We are also able to customize the way of identifying what the requested session id is.
		</p>
		<form  class="form-inline"  role="form"  action="/test" method="post">
			<label for="attributeValue">Attribute Name</label>
			<input id="attributeValue" type="text" name="attributeName">
			<label for="attributeValue">Attribute Value</label>
			<input id="attributeValue" type="text" name="attributeValue" />
			<input type="submit" value="Set Attribute" />
		</form>
		<hr />
		<table  
				class="table table-striped">
			<thead>
				<tr>
					<th>Attribute Name</th>
					<th>Attribute Value</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${sessionScope}" var="attr">
					<tr>
						<td><c:out value="${attr.key}" /></td>
						<td><c:out value="${attr.value}" /></td>
					</tr>
				</c:forEach>
			</tbody>
			</table>
	</div>
</body>
</html>