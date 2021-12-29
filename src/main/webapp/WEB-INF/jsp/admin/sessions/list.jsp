<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled sesija</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
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
			<h2 class="title2 p-class">ADMIN : Pregled sesija za predavanje ${ selectedLecture.subject.name }
				(nastavnik ${ selectedLecture.teacher.firstName } ${ selectedLecture.teacher.lastName })</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		<a href="/api/v1/project/sessions/register_session">Dodaj sesiju</a>
		
		<br />
		<br />
		
		<c:if test="${ !empty sessions }">
			<table>
				<tr>
					<th class="table_header">Tema</th>
					<td width="10" />
					<th class="table_header">Datum</th>
					<td width="10" />
				</tr>
				
				<c:forEach items="${ sessions }" var="s">
					<tr>
						<td class="table_cell">${ s.topic }</td>
						<td width="10" />
						<td class="table_cell">${ s.date }</td>
						<td width="10" />
						<td class="table_cell"><a href="/api/v1/project/sessions/delete?idToDelete=${ s.id }">Obriši</a></td>
						<td width="10" />
						<td class="table_cell"><a href="/api/v1/project/grade/get_students_for_absences?sessionId=${ s.id }">Unesi izostanke</a></td>
						<td width="10" />
					</tr>
				</c:forEach>	
				
			</table>
		</c:if>
		
		<c:if test="${ empty sessions }">
			<p>Nema unetih sesija.</p>
		</c:if>
		
		<br />
		
		<c:if test="${ !empty deleteSuccessMsg }">
			<span class="success">${ deleteSuccessMsg }</span>
			<br />
		</c:if>
		
		<a href="/api/v1/project/lectures/list">Nazad na pregled svih predavanja</a>
		
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>