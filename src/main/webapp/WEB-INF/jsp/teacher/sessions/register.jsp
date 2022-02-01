<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registrovanje sesije</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/teacher/home" class="sidenav-sel">Glavna</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">NASTAVNIK : Registrovanje sesije</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<form action="/api/v1/project/sessions/register" method="post">
		
			<table>
				<tr>
					<td>Tema</td>
					<td><input type="text" name="topic" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>Date</td>
					<td><input type="date" name="date" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>
						<br />
						<input type="submit" value="      Registruj      " />
					</td>
				</tr>
			</table>
			
		</form>
		
		<span class="success">${ sessionRegisterSuccessMsg }</span>
		<span class="error">${ sessionRegisterMsg }</span>
		
		<br />
		<a href="/api/v1/project/sessions/list?idToUpdate=${ selectedLecture.id }">Nazad na pregled sesija</a>
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>