<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled predmeta</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/admin/home">Glavna</a>
		<a href="/api/v1/project/registration/home">Korisnici</a> 
		<a href="/api/v1/project/subjects/home" class="sidenav-sel">Predmeti</a>
		<a href="#">Opcija #3</a>
		<a href="/api/v1/project/admin/logs">Pregled logova</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">ADMIN : Pregled predmeta</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<table>
			<tr>
				<th class="table_header">Naziv</th>
				<td width="10" />
				<th class="table_header">Broj časova</th>
				<td width="10" />
				<th class="table_header">Godina akreditacije</th>
				<td width="10" />
			</tr>
			
			<c:forEach items="${ subjects }" var="s">
				<tr>
					<td class="table_cell">${ s.name }</td>
					<td width="10" />
					<td class="table_cell">${ s.totalHours }</td>
					<td width="10" />
					<td class="table_cell">${ s.yearAccredited }</td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/subjects/delete?idToDelete=${ s.id }">Obriši</a></td>
					<td width="10" />
					<td class="table_cell"><a href="/api/v1/project/subjects/update_subject?idToUpdate=${ s.id }">Ažuriraj</a></td>
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