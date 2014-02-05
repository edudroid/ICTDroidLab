<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0" />	
	<title>DroidLab portal</title>
	<link rel="stylesheet" href="/css/style.css" type="text/css">
	<link rel="icon" type="image/png" href="/icon.png">
</head>
<%
// Retrieve session information
String email = (String)session.getAttribute(Constants.EMAIL);
boolean loggedIn = email != null;
%>
<body>
	<div id="header">
		<div>
			<div class="logo">
				<a href="">DroidLab</a>
			</div>
			<ul id="navigation">
<% if (request.getParameter("selected").equals(Constants.INDEX)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
 					<a href="/">Home</a>
				</li>
<% if (request.getParameter("selected").equals(Constants.FEATURES)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
					<a href="/details">Features</a>
				</li>
<% if (request.getParameter("selected").equals(Constants.TEAM)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
					<a href="/team">Team</a>
				</li>
<% if (request.getParameter("selected").equals(Constants.NEWS)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
					<a href="/news">News</a>
				</li>
<% if (request.getParameter("selected").equals(Constants.REG_FORM)||
		request.getParameter("selected").equals(Constants.PROFILE)||
		request.getParameter("selected").equals(Constants.DEVICES)||
		request.getParameter("selected").equals(Constants.DEVICE)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>

<% if (loggedIn) {
	%>
					<a href="/userhome">My Lab</a>
	<% 
} else {
	%>
					<a href="/regform">Register</a>
	<%
}
%>
				</li>
			</ul>
		</div>
	</div>