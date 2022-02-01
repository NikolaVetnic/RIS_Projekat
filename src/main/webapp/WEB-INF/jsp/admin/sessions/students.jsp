<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Unos izostanaka</title>
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
			<h2 class="title2 p-class">ADMIN : Unos izostanaka</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<c:if test="${ !empty students }">
		
			<table>
				<tr>
					<th class="table_header">ID</th>
					<td width="10" />
					<th class="table_header">Prezime</th>
					<td width="10" />
					<th class="table_header">Ime</th>
					<td width="10" />
					<th class="table_header">JMBG</th>
					<td width="10" />
					<th class="table_header">Korisničko ime</th>
				</tr>
				
				<c:forEach items="${ students }" var="s">
					<tr>
						<td class="table_cell">${ s.id }</td>
						<td width="10" />
						<td class="table_cell">${ s.lastName }</td>
						<td width="10" />
						<td class="table_cell">${ s.firstName }</td>
						<td width="10" />
						<td class="table_cell">${ s.jmbg }</td>
						<td width="10" />
						<td class="table_cell">${ s.username }</td>
						<td width="10" />
						<td class="table_cell"><a href="/api/v1/project/grade/add_absence?studentId=${ s.id }">Unesi izostanak</a></td>
						<td width="10" />
					</tr>
				</c:forEach>	
				
			</table>
			
		</c:if>
		
		<c:if test="${ empty students }">
			Na predavanju nema prijavljenih učenika ili su već svi učenici obeleženi kao odsutni.
			<br />
		</c:if>
		
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