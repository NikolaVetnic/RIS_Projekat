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
		<a href="/api/v1/project/admin/home" class="sidenav-sel">Glavna</a>
		<a href="/api/v1/project/registration/home">Korisnici</a> 
		<a href="/api/v1/project/subjects/home">Predmeti</a>
		<a href="#">Opcija #3</a>
		<a href="/api/v1/project/admin/logs">Pregled logova</a>
		<br>
		<a href="/">Izloguj se</a>
	</div>

	<div class="main">
		<div class="header">
			<h2 class="title2 p-class">ADMIN : Glavna stranica</h2>
			<p class="p-class">Ulogovani ste kao : <span class="username p-class">${ user.username } </span></p>
		</div>
		
		<br />
		
		<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec id ligula efficitur nisl pulvinar accumsan in eget leo. Fusce eleifend sit amet libero eget faucibus. Nam ut sagittis velit, in malesuada quam. Mauris id congue nisl. Curabitur vestibulum ultrices tristique. Fusce lobortis erat quis sollicitudin fermentum. Etiam pharetra aliquet felis, sed laoreet felis porttitor et. Sed vitae facilisis est, ultricies hendrerit justo. Suspendisse condimentum purus ante, et vehicula turpis aliquam eget. Nam nunc nisl, scelerisque ac mauris ut, tristique sodales augue. Proin tristique, tellus id ullamcorper pharetra, libero est convallis nibh, non dignissim justo turpis et ipsum. Vivamus gravida libero sit amet risus eleifend, non tempor dolor vehicula. Proin at mi odio. Vestibulum molestie sit amet enim vitae mollis.</p>
		
	</div>
	
	<div class="footer">
	  	School System Project @ Razvoj informacionih sistema, 2021.
	</div>
	
</body>
</html>