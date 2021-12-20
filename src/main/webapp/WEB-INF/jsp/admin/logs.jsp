<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled logova</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<h2 class="title2">ADMIN : Pregled logova</h2>
	
	<p>Ulogovani ste kao : <span class="username">${ userLoginDTO.username } </span></p>
	
	<br />
	<a href="/api/v1/project/admin/home">Nazad na glavnu stranicu</a>
	
	<c:forEach items="${ logs }" var="l">
		<p class="logs">${ l }</p>
	</c:forEach>
	
	<a href="/api/v1/project/admin/home">Nazad na glavnu stranicu</a>
	
</body>
</html>