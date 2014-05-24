<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="java.util.Map"%>
<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.List" %>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
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
User user = UserManager.checkUser(session, request, response);
boolean loggedIn = user != null;
Key userKey = null;
String email = null;
if (loggedIn) {
	userKey = user.getKey();
	email = user.getEmail();
}
%>
<body>
	<div id="header">
		<div>
			<div class="logo">
				<a href=""></a>
			</div>
			<ul id="navigation">
<% if (request.getParameter("selected").equals(Constants.INDEX)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
 					<a href="/">DroidLab</a>
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
		request.getParameter("selected").equals(Constants.LOGIN)||
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
					<a href="/loginform">Log in</a>
	<%
}
%>
				</li>
			</ul>
		</div>
	</div>