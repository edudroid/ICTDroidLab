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
	String email = (String)session.getAttribute(Constants.EMAIL);
	if (email == null) {
		response.sendRedirect("/loginform");
		return;
	}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.USER_ROOT %>" />
</jsp:include>
		<div>
			<h1>
				Welcome to your lab!
			</h1>
			
			<table>
				<tr>
					<td><a href="/devices">Devices</a></td>
				</tr>
				<tr>
					<td><a href="/profile">Profile</a></td>
				</tr>
				<tr>
					<td><a href="/modules">Modules</a></td>
				</tr>
				<tr>
					<td><a href="/uploadmodule">Upload Module</a></td>
				</tr>
				<tr>
					<td><a href="/results">Results</a></td>
				</tr>
				<tr>
					<td><a href="/measurement">Measurement</a></td>
				</tr>
			</table>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />