<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled ocena</title>
<style type="text/css"><%@ include file="/resources/css/student.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/student/home" class="sidenav-sel">Glavna</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">UČENIK : Pregled ocena i izostanaka (učenik ${ student.lastName } ${ student.firstName })</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<c:forEach items="${ gradeCards }" var="gc">
		
			<h3>${ gc.lecture.subject.name } (nastavnik ${ gc.lecture.teacher.lastName } ${ gc.lecture.teacher.firstName })</h3>
			<blockquote>
				Ocene : 
					<blockquote>
						<table action="/api/v1/project/grade/update_grade" method="post">
							<c:forEach items="${ gc.grades }" var="g">
								<tr>
									<td>[ ${ g.grade } ]</td>
									<td width="10" />
									
									<c:forEach items="${ intArray5 }" var="num">
										<td>
											<a href="/api/v1/project/grade/update_grade?gradeId=${ g.id }&newGrade=${ num }">-> ${ num }</a>
										</td>
										<td width="10" /> 
									</c:forEach>
									
									<td><a href="/api/v1/project/grade/delete?gradeCardId=${ gc.id }&gradeId=${ g.id }">Obriši ocenu</a></td>
									<td width="10" />
									
								</tr>
							</c:forEach>
							
							<tr>
								<td>[${ gc.average() }]</td>
								<td width="10" />
								<td>AVG</td>
								<td width="10" />
								<td></td>
								<td width="10" />
							</tr>
							
						</table>
					</blockquote>
				<p>Izostanci :</p>
				<blockquote>
					<table>
						<c:forEach items="${ gc.absences }" var="ab">
							<tr>
								<td>${ ab.date }</td>
								<td width="10" />
								<td width="500" >
									<c:if test="${ !empty ab.note }">
										${ ab.note }
									</c:if>
									<c:if test="${ empty ab.note }">
										Nema opravdanja.
									</c:if>
								</td>
								<td width="10" />
								<td>
									<a href="/api/v1/project/grade/get_absence?idToUpdate=${ ab.id }">Ažuriraj opravdanje</a>
								</td>
							</tr>
						</c:forEach>
					</table>
				</blockquote>
			</blockquote>
			
			<hr>
			
		</c:forEach>
		
		<table>
			<tr>
				<td width="200" >Prosek učenika</td>
				<td>[ ${ student.average() } ]</td>
			</tr>
		</table>
		
		<hr>
		
		<br />
		<a href="/api/v1/project/student/home">Nazad na glavnu</a> 
		<br />
		<br />
		<br />
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>