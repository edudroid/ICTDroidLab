<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.PROFILE %>" />
</jsp:include>
<%
	String firstName = "";
	String email = (String)session.getAttribute(Constants.EMAIL);
%>
<div id="contents">
<div style="float: right; margin-top: -40px; margin-right: 60px;">
<%= email %>, <a href="/websignout">sign out</a>
</div>
	<div id="tagline" class="clearfix">
		<div>
			<ul id="userbox">
			<li><a>Devices</a></li>
			<li><a>Profile</a></li>
			</ul>
		</div>
		<div class="main_content">
			<h1>
				Welcome to your lab!
			</h1>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />