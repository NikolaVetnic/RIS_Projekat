<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ažuriranje opravdanja</title>
<style type="text/css"><%@ include file="/resources/css/teacher.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/teacher/home" class="sidenav-sel">Glavna</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">NASTAVNIK : Ažuriranje opravdanja (učenik ${ student.lastName } ${ student.firstName })</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<form action="/api/v1/project/grade/add_absence_note" method="post">
		
			<table>
				<tr>
					<td>Opravdanje</td>
					<td><input type="text" name="absence_note" style="width: 390px" /></td>
				</tr>
				<tr>
					<td>
						<br />
						<input type="submit" value="      Unesi      " />
					</td>
				</tr>
			</table>
			
		</form>
		
		<br />
		<a href="/api/v1/project/grade/grade_cards?idToUpdate=${ student.id }">Nazad na ocene</a>
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>