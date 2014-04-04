<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
User user = UserManager.checkUser(session, request, response);
if (user == null) {
	response.sendRedirect("/loginform");
	return;
}
%>
<div style="float: right; margin-top: -40px; margin-right: 60px;">
<%= user.getEmail() %>, <a href="/signout?<%= Constants.WEB %>=true">sign out</a>
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

<!-- USERS -->
<% if (user.getRole().equals(Constants.ROLE_ADMIN)){ %>
	<% if (request.getParameter("selected").equals(Constants.USERS)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/users">Users</a>
			</li>
<% } %>

<!-- DEVICES -->
<% if (user.getRole().equals(Constants.ROLE_USER)){ %>
	<% if (request.getParameter("selected").equals(Constants.DEVICES)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/devices">Devices</a>
			</li>
<% } %>

<!-- PROFILE -->
	<% if (request.getParameter("selected").equals(Constants.PROFILE)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/profile">Profile</a>
			</li>		

<!-- MODULES -->
<% if (user.getRole().equals(Constants.ROLE_RESEARCHER)){ %>
	<% if (request.getParameter("selected").equals(Constants.MODULES)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="modules">Modules</a>
			</li>
<% } %>

<!-- UPLOAD MODULE -->
<% if (user.getRole().equals(Constants.ROLE_RESEARCHER)){ %>
	<% if (request.getParameter("selected").equals(Constants.UPLOADMODULE)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/uploadmodule">Upload Module</a>
			</li>
<% } %>

<!-- RESULTS -->
<% if (user.getRole().equals(Constants.ROLE_RESEARCHER)){ %>
	<% if (request.getParameter("selected").equals(Constants.RESULTS)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/results">Results</a>
			</li>
<% } %>

<!-- MEASUREMENTS -->
<% if (user.getRole().equals(Constants.ROLE_RESEARCHER)){ %>
	<% if (request.getParameter("selected").equals(Constants.MEASUREMENT)) { %>
					<li class="active">
	<% } else { %>
					<li>
	<% } %>
				<a href="/measurement">Measurement</a>
			</li>
<% } %>	
		</ul>
	</div>