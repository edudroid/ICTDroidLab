<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.LOGIN %>" />
</jsp:include>
<div id="contents">
	<div id="tagline" class="clearfix">
		<h1>Welcome back!</h1>
		<div class="main_content">
		<p>
			Sign in, and start measuring.
		</p>
<%
if (request.getAttribute(Constants.ERROR) != null){
	%>
		<span class="error"><%= request.getAttribute(Constants.ERROR)%></span>
	<%
}
%>
		<form action="/login" method="post" class="register">
			<input type="text" name="<%= Constants.EMAIL %>" id="<%= Constants.EMAIL %>" placeholder="Email" onFocus="this.select();" onMouseOut="javascript:return false;"/>
			<input type="password" name="<%= Constants.PASSWORD %>" id="<%= Constants.PASSWORD %>" placeholder="Password" onFocus="this.select();" onMouseOut="javascript:return false;"/>
			<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
			<input type="submit" value="Login"/>
		</form>
		<p style="margin-top: 40px">
			Don't have an account? <a href="/regform">Register</a> now for free!
		</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />