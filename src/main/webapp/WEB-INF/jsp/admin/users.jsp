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

	<h2 class="title2">ADMIN : Pregled svih korisnika</h2>
	
	<p>Ulogovani ste kao : ${ userLoginDTO.username } </p>
	
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
				<td class="table_cell">${ u.role }</td>
			</tr>
		</c:forEach>	
		
	</table>
	<br />
	<a href="/api/v1/project/admin/home">Nazad na glavnu stranicu</a>
	
</body>
</html>