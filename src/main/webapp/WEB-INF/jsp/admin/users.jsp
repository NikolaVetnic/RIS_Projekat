<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled korisnika</title>
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
			<h2 class="title2 p-class">ADMIN : Pregled korisnika</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<table>
			<tr>
				<th class="table_header">Ime</th>
				<td width="10" />
				<th class="table_header">Prezime</th>
				<td width="10" />
				<th class="table_header">JMBG</th>
				<td width="10" />
				<th class="table_header">Email</th>
				<td width="10" />
				<th class="table_header">Korisničko ime</th>
				<td width="10" />
				<th class="table_header">Uloga</th>
			</tr>
			
			<c:forEach items="${ users }" var="u">
				<tr>
					<td class="table_cell"><c:if test="${ u.role != 'ADMIN'}">${ u.firstName }</c:if></td>
					<td width="10" />
					<td class="table_cell"><c:if test="${ u.role != 'ADMIN'}">${ u.lastName }</c:if></td>
					<td width="10" />
					<td class="table_cell"><c:if test="${ u.role == 'STUDENT'}">${ u.jmbg }</c:if></td>
					<td width="10" />
					<td class="table_cell">
						<c:if test="${ u.role == 'TEACHER' || u.role == 'PARENT'}">
							<a href="mailto:${ u.email }">${ u.email }</a>
						</c:if>
					</td>
					<td width="10" />
					<td class="table_cell">${ u.username }</td>
					<td width="10" />
					<td class="table_cell">
						<c:if test="${ u.role == 'ADMIN' }">
							administrator
						</c:if>
						<c:if test="${ u.role == 'PARENT' }">
							roditelj
						</c:if>
						<c:if test="${ u.role == 'STUDENT' }">
							učenik
						</c:if>
						<c:if test="${ u.role == 'TEACHER' }">
							nastavnik
						</c:if>
					</td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/admin/delete?idToDelete=${ u.id }">Obriši</a></td>
					<td width="10" />
					<td class="table_cell">
						<c:if test="${ u.role == 'ADMIN' }">
							<a href="/api/v1/project/update/update_admin?idToUpdate=${ u.id }">Ažuriraj</a>
						</c:if>
						<c:if test="${ u.role == 'PARENT' }">
							<a href="/api/v1/project/update/update_parent?idToUpdate=${ u.id }">Ažuriraj</a>
						</c:if>
						<c:if test="${ u.role == 'STUDENT' }">
							<a href="/api/v1/project/update/update_student?idToUpdate=${ u.id }">Ažuriraj</a>
						</c:if>
						<c:if test="${ u.role == 'TEACHER' }">
							<a href="/api/v1/project/update/update_teacher?idToUpdate=${ u.id }">Ažuriraj</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>	
			
		</table>
		<br />
		<c:if test="${ !empty deleteSuccessMsg }">
			<span class="success">${ deleteSuccessMsg }</span>
			<br />
		</c:if>
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