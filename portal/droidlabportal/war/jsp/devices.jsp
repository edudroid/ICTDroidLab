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
	Query query = new Query(Constants.MYDEVICE_TABLE_NAME, userKey);
	List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(10));
	for (Entity entity : devices) {
%>
				<tr>
					<td>
						<a href="/jsp/device.jsp?<%= Constants.IMEI %>=<%= entity.getProperty(Constants.MYDEVICE_IMEI_COLUMN) %>"><%= entity.getProperty(Constants.MYDEVICE_IMEI_COLUMN) %></a>
					</td>
				</tr>
<%
	}
%>
			</table>
			<form action="/registerdevice" method="post" class="register">
				<input type="number" name="<%= Constants.DEVICES_IMEI_COLUMN %>" id="<%= Constants.DEVICES_IMEI_COLUMN %>" placeholder="IMEI" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.DEVICES_SDK_COLUMN %>" id="<%= Constants.DEVICES_SDK_COLUMN %>" placeholder="SDK" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.DEVICES_CELLULAR_COLUMN %>" id="<%= Constants.DEVICES_CELLULAR_COLUMN %>" placeholder="CELLULAR" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.DEVICES_WIFI_COLUMN %>" id="<%= Constants.DEVICES_WIFI_COLUMN %>" placeholder="WIFI" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.DEVICES_GPS_COLUMN %>" id="<%= Constants.DEVICES_GPS_COLUMN %>" placeholder="GPS" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.DEVICES_BLUETOOTH_COLUMN %>" id="<%= Constants.DEVICES_BLUETOOTH_COLUMN %>" placeholder="BLUETOOTH" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
				<input type="submit" value="Add device"/>
			</form>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />