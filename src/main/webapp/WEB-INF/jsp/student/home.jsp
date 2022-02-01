<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Glavna stranica</title>
<style type="text/css"><%@ include file="/resources/css/styles.css" %> </style>
</head>
<body>

	<div class="sidenav">
		<a href="/api/v1/project/student/home" class="sidenav-sel">Glavna</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">UČENIK : Glavna stranica</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec id ligula efficitur nisl pulvinar accumsan in eget leo. Fusce eleifend sit amet libero eget faucibus. Nam ut sagittis velit, in malesuada quam. Mauris id congue nisl.</p>
		
		<h3>OPCIJE</h3>
		<ul>
			<li><a href="/api/v1/project/grade/grade_cards?idToUpdate=${ user.id }">Pregled sopstvenih ocena i izostanaka</a></li>
		</ul>
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>