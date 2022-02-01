<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Upravljanje korisnicima</title>
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
			<h2 class="title2 p-class">ADMIN : Upravljanje korisnicima</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<h3>PREGLED</h3>
		<ul>
			<li><a href="/api/v1/project/admin/users">Pregled svih korisnika</a></li>
			<li><a href="/api/v1/project/classes/list">Pregled svih odelenja</a></li>
		</ul>
		<h3>KORISNICI</h3>
		<ul>
			<li><a href="/api/v1/project/registration/register_admin">Registracija admina</a></li>
			<li><a href="/api/v1/project/registration/register_teacher">Registracija nastavnika</a></li>
			<li><a href="/api/v1/project/registration/register_parent">Registracija roditelja</a></li>
			<li><a href="/api/v1/project/registration/register_student">Registracija učenika</a></li>
		</ul>
		<h3>ODELENJA</h3>
		<ul>
			<li><a href="/api/v1/project/classes/register_school_class">Registracija odelenja</a></li>
		</ul>
		<h3>IZVEŠTAJI</h3>
		<ul>
			<li><a href="/api/v1/project/admin/all_students_report">Generisanje spiska učenika po odelenjima</a></li>
		</ul>
	</div>
	
	<br />
	<br />
	<br />
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>