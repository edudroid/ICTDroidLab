<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.REG_FORM %>" />
</jsp:include>
<div id="contents">
	<div id="tagline" class="clearfix">
		<h1>Be part of DroidLab!</h1>
		<p>
			Are you a researcher in need of a sensor network, a developer wanting to bootstrap a crowdsensing application, or a smartphone owner who wants to contribute, this is the place to start.
		</p>
		<p>
			Register and be part of the DroidLab community!
		</p>
<%
if (request.getAttribute(Constants.ERROR) != null){
	%>
		<span class="error"><%= request.getAttribute(Constants.ERROR)%></span>
	<%
}
%>
			<form action="/registeruser" method="post" class="register">
				<input type="text" name="<%= Constants.EMAIL %>" id="<%= Constants.EMAIL %>" placeholder="Email" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="password" name="<%= Constants.PASSWORD %>" id="<%= Constants.PASSWORD %>" placeholder="Password" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="password" name="<%= Constants.PASSWORDCHECK %>" id="<%= Constants.PASSWORDCHECK %>" placeholder="Password check" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
				<input type="submit" value="Register"/>
			</form>
		</div>
	</div>
<jsp:include page="/jsp/footer.jsp" />