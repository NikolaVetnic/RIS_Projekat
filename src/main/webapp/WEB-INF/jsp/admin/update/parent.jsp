<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ažuriranje roditelja</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/admin/home">Glavna</a>
		<a href="/api/v1/project/registration/home" class="sidenav-sel">Korisnici</a> 
		<a href="/api/v1/project/subjects/home">Predmeti</a>
		<a href="#">Opcija #3</a>
		<a href="/api/v1/project/admin/logs">Pregled logova</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">ADMIN : Ažuriranje roditelja</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<form action="/api/v1/project/update/parents" method="post">
		
			<table>
				<tr>
					<td>Ime</td>
					<td><input type="text" name="firstName" value="${ parentToUpdate.firstName }"/></td>
				</tr>
				<tr>
					<td>Prezime</td>
					<td><input type="text" name="lastName" value="${ parentToUpdate.lastName }"/></td>
				</tr>
				<tr>
					<td>Lozinka</td>
					<td><input type="text" name="password"/></td>
				</tr>
				<tr>
					<td>Lozinka (ponovo)</td>
					<td><input type="text" name="confirmPassword"/></td>
				</tr>
				<tr>
					<td>
						<br />
						<input type="submit" value="      Ažuriraj      " />
					</td>
				</tr>
			</table>
			
		</form>
		
		<span class="success">${ parentUpdateSuccessMsg }</span>
		<span class="error">${ parentUpdateMsg }</span>
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>