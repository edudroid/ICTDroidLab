<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICES %>" />
</jsp:include>
<%
	String email = (String)session.getAttribute(Constants.EMAIL);
	if (email == null) {
		response.sendRedirect("/loginform");
		return;
	}
	Key userKey = null;
	try {
		userKey = (Key)session.getAttribute(Constants.USER_KEY);
	} catch (Exception e) {
		response.sendRedirect("/loginform");
		return;
	}
	if (userKey == null) {
		response.sendRedirect("/loginform");
		return;
	}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICES %>" />
</jsp:include>
		<div>
			<h1>
				Devices
			</h1>
			<table>
<%
	// List user's devices
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query query = new Query(Constants.DEVICE_TABLE_NAME, userKey);
	List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(10));
	for (Entity entity : devices) {
%>
				<tr>
					<td>
						<a href="/jsp/device.jsp?<%= Constants.IMEI %>=<%= entity.getProperty(Constants.DEVICE_IMEI_COLUMN) %>"><%= entity.getProperty(Constants.DEVICE_IMEI_COLUMN) %></a>
					</td>
				</tr>
<%
	}
%>
			</table>
			<form action="/registerdevice" method="post" class="register">
				<input type="text" name="<%= Constants.IMEI %>" id="<%= Constants.IMEI %>" placeholder="IMEI" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
				<input type="submit" value="Add device"/>
			</form>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />