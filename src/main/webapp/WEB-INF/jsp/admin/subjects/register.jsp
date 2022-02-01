<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registrovanje predmeta</title>
<style type="text/css"><%@ include file="/resources/css/admin.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/admin/home">Glavna</a>
		<a href="/api/v1/project/registration/home">Korisnici</a> 
		<a href="/api/v1/project/subjects/home" class="sidenav-sel">Predmeti</a>
		<a href="/api/v1/project/admin/logs">Pregled logova</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">ADMIN : Registrovanje predmeta</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<form action="/api/v1/project/subjects/register" method="post">
		
			<table>
				<tr>
					<td>Naziv</td>
					<td><input type="text" name="name" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>Broj časova</td>
					<td><input type="text" name="totalHours" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>Godina akreditacije</td>
					<td><input type="text" name="yearAccredited" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>
						<br />
						<input type="submit" value="      Registruj      " />
					</td>
				</tr>
			</table>
			
		</form>
		
		<span class="success">${ subjectRegisterSuccessMsg }</span>
		<span class="error">${ subjectRegisterMsg }</span>
		
		<br />
		<a href="/api/v1/project/subjects/list">Pregled svih predmeta</a>
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>