<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Glavna stranica</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<h2 class="title2">ADMIN : Glavna stranica</h2>

	<p>Ulogovani ste kao : <span class="username">${ userLoginDTO.username } </span></p>
	
	<ul>
		<li><a href="/api/v1/project/admin/users">Pregled svih korisnika</a></li>
		<li><a href="/api/v1/project/admin/logs">Pregled logova</a></li>
		<li><a href="/">Izloguj se</a></li>
	</ul>
	
</body>
</html>