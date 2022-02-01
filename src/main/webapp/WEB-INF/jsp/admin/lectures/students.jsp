<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Ažuriranje učenika</title>
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
			<h2 class="title2 p-class">ADMIN : Ažuriranje učenika za predavanje ${ selectedLecture.subject.name } 
				(nastavnik ${ selectedLecture.teacher.firstName } ${ selectedLecture.teacher.lastName })</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<h3>AŽURIRANJE UČENIKA ZA PREDAVANJE</h3>
		
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
					<td class="table_cell">
						<a href="/api/v1/project/lectures/enroll_or_remove_student?idToUpdate=${ s.id }">
							<c:choose>
  								<c:when test="${ s.isTakingLecture(selectedLecture.id) }">
  									Izbaci
								</c:when>
  								<c:otherwise>
  									Ubaci
								</c:otherwise>
  							</c:choose>
						</a>
					</td>
					<td width="10" />
					<td class="table_cell">
						<a href="/api/v1/project/lectures/grade_student?idToUpdate=${ s.id }">
							<c:if test="${ s.isTakingLecture(selectedLecture.id) }">
								Oceni
							</c:if>
						</a>
					</td>
					<td width="10" />
				</tr>
			</c:forEach>	
			
		</table>
		<br />
		<c:if test="${ !empty updateSuccessMsg }">
			<span class="success">${ updateSuccessMsg }</span>
			<br />
		</c:if>
		<c:if test="${ !empty updateErrorMsg }">
			<span class="error">${ updateErrorMsg }</span>
			<br />
		</c:if>
		<a href="/api/v1/project/lectures/list">Nazad na pregled svih predavanja</a>
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>