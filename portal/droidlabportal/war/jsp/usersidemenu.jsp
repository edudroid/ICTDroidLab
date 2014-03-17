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
<%= email %>, <a href="/signout?<%= Constants.WEB %>=true">sign out</a>
</div>
	<div>
		<ul id="userbox">
<% if (request.getParameter("selected").equals(Constants.USER_ROOT)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/userhome">Dashboard</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.DEVICES)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/devices">Devices</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.PROFILE)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/profile">Profile</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.MODULES)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="modules">Modules</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.UPLOADMODULE)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/uploadmodule">Upload Module</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.RESULTS)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/results">Results</a>
		</li>
<% if (request.getParameter("selected").equals(Constants.MEASUREMENT)) { %>
				<li class="active">
<% } else { %>
				<li>
<% } %>
			<a href="/measurement">Measurement</a>
		</li>		
		</ul>
	</div>