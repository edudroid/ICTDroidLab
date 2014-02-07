<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
	String email = (String)session.getAttribute(Constants.EMAIL);
%>
<div style="float: right; margin-top: -40px; margin-right: 60px;">
<%= email %>, <a href="/websignout">sign out</a>
</div>
	<div>
		<ul id="userbox">
		<li><a>Devices</a></li>
		<li><a>Profile</a></li>
		</ul>
	</div>