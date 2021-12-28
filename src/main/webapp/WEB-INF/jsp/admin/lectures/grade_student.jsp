<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Ocenjivanje učenika</title>
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
			<h2 class="title2 p-class">ADMIN : Ocenjuje se učenik ${ studentToGrade.lastName } ${ studentToGrade.firstName }</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<form action="/api/v1/project/lectures/grade" method="post">
		
			<table>
				<tr>
					<td>Ocena</td>
					<td>
						<select name="grade_num" style="width: 400px">
							<option disabled selected value> -- odaberite ocenu -- </option>
							<c:forEach items="${ intArray5 }" var="num">
								<option value="${ num }">${ num }</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td>
						<br />
						<input type="submit" value="      Oceni      " />
					</td>
				</tr>
			</table>
			
		</form>
		
		<span class="success">${ gradeUpdateSuccessMsg }</span>
		<span class="error">${ gradeUpdateMsg }</span>
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>