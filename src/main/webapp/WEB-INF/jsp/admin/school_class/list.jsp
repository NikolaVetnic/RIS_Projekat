<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled odelenja</title>
<style type="text/css"><%@ include file="/resources/css/admin.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/admin/home">Glavna</a>
		<a href="/api/v1/project/registration/home" class="sidenav-sel">Korisnici</a> 
		<a href="/api/v1/project/subjects/home">Predmeti</a>
		<a href="/api/v1/project/admin/logs">Pregled logova</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">ADMIN : Pregled odelenja</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<table>
			<tr>
				<th class="table_header">Razred</th>
				<td width="10" />
				<th class="table_header">Odelenje</th>
				<td width="10" />
				<th class="table_header">Generacija</th>
				<td width="10" />
			</tr>
			
			<c:forEach items="${ schoolClasses }" var="s">
				<tr>
					<td class="table_cell">${ s.classNo }</td>
					<td width="10" />
					<td class="table_cell">${ s.sectionNo }</td>
					<td width="10" />
					<td class="table_cell">${ s.generation }</td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/classes/class_students?idToUpdate=${ s.id }">Pregled učenika</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/classes/all_students?idToUpdate=${ s.id }">Ažuriranje učenika</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/classes/delete?idToDelete=${ s.id }">Obriši</a></td>
					<td width="10" />
				</tr>
			</c:forEach>	
			
		</table>
		<br />
		<a href="/api/v1/project/registration/home">Nazad na korisnike</a> 
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>