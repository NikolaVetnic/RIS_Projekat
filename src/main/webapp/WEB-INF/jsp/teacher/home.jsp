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

	<h2 class="title2">NASTAVNIK : Glavna stranica</h2>

	<p>Ulogovani ste kao : <span class="username">${ user.username } </span></p>
	
	<ul>
		<li><a href="/api/v1/project/teacher/grades">Pregled svih ocena</a></li>
	</ul>
	
</body>
</html>