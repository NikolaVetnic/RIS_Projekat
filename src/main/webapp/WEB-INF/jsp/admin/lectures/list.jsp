<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled predavanja</title>
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
			<h2 class="title2 p-class">ADMIN : Pregled predavanja</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<table>
			<tr>
				<th class="table_header">Predmet</th>
				<td width="10" />
				<c:if test="${ role != 'teacher' }">
					<th class="table_header">Nastavnik</th>
					<td width="10" />
				</c:if>
				<th class="table_header">Godina održavanja</th>
				<td width="10" />
				<th class="table_header">Polugodište</th>
				<td width="10" />
			</tr>
			
			<c:forEach items="${ lectures }" var="l">
				<tr>
					<td class="table_cell">${ l.subject.name }</td>
					<td width="10" />
					<c:if test="${ role != 'teacher' }">
						<td class="table_cell">${ l.teacher.firstName } ${ l.teacher.lastName }</td>
						<td width="10" />
					</c:if>
					<td class="table_cell">${ l.year }</td>
					<td width="10" />
					<td class="table_cell">
						<c:if test="${ l.semester == 'WINTER'}">zimski</c:if>
						<c:if test="${ l.semester == 'SUMMER'}">letnji</c:if>
					</td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/lectures/delete?idToDelete=${ l.id }">Obriši</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/lectures/update_lecture?idToUpdate=${ l.id }">Ažuriraj</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/lectures/all_students?idToUpdate=${ l.id }">Učenici</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/sessions/list?idToUpdate=${ l.id }">Sesije</a></td>
					<td width="10" />
				</tr>
			</c:forEach>	
			
		</table>
		<br />
		<span class="success">${ deleteSuccessMsg }</span>
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>