<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Indeks</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>
	
	<h2 class="title2">Prijava korisnika</h2>
	
	<form action="/api/v1/project/login" method="post">
		
		<table>
			<tr>
				<td>Korisničko ime</td>
				<td><input type="text" name="username" /></td>
			</tr>
			<tr>
				<td>Lozinka</td>
				<td><input type="text" name="pwd" /></td>
			</tr>
			<tr>
				<td>
					<br />
					<input type="submit" value="      Login      " />
				</td>
			</tr>
		</table>
		
	</form>
	
	<span class="error">${ userLoginMsg }</span>

</body>
</html>