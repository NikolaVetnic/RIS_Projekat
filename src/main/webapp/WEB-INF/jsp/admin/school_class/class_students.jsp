<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Prikaz učenika</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
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
			<h2 class="title2 p-class">ADMIN : Prikaz učenika</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<h3>PRIKAZ UČENIKA ZA ODELENJE ${ selectedSchoolClass.getFormattedString() }</h3>
		
		<c:if test="${ empty students }">
			<p>Odeljenje nema učenika.</p>
		</c:if>
		
		<c:if test="${ !empty students }">

			<br />
		
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
					<td width="10" />
					<th class="table_header">Odelenje</th>
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
						<td class="table_cell">
							<c:if test="${ !empty s.schoolClass }">
								${ s.schoolClass.getFormattedString() }
							</c:if>
							<c:if test="${ empty s.schoolClass }">
								bez odelenja
							</c:if>
						</td>
						<td width="10" />
					</tr>
				</c:forEach>
			</table>
			
			<br />
			
		</c:if>
		<a href="/api/v1/project/classes/list">Nazad na pregled svih odelenja</a>
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>