<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled dece</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/${ role }/home" class="sidenav-sel">Glavna</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">RODITELJ : Pregled korisnika</h2>
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
				<th class="table_header">Korisničko ime</th>
			</tr>
			
			<c:forEach items="${ users }" var="u">
				<tr>
					<td class="table_cell"><c:if test="${ u.role != 'ADMIN'}">${ u.firstName }</c:if></td>
					<td width="10" />
					<td class="table_cell"><c:if test="${ u.role != 'ADMIN'}">${ u.lastName }</c:if></td>
					<td width="10" />
					<td class="table_cell"><c:if test="${ u.role == 'STUDENT'}">${ u.jmbg }</c:if></td>
					<td width="10" />
					<td class="table_cell">${ u.username }</td>
					<td width="10" />
					<td class="table_cell">
						<c:if test="${ u.role == 'STUDENT' }">
							<a href="/api/v1/project/grade/grade_cards?idToUpdate=${ u.id }">Ocene i izostanci</a>
						</c:if>
					</td>
					<td width="10" />
				</tr>
			</c:forEach>	
			
		</table>
		<br />
		<a href="/api/v1/project/${ role }/home">Nazad na glavnu</a> 
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>