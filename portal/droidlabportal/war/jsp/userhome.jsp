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
	<div id="tagline" class="clearfix">
		<h1>Welcome, <%= firstName %></h1>
		<div>
			<p>
				Your email address is: <%= email %>
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />