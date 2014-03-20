<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
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
				My Devices
			</h1>

<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	// Run an ancestor query to ensure we see the most up-to-date
	// view of the Greetings belonging to the selected Guestbook.
	Query query = new Query(Constants.DEVICE_TABLE_NAME,userKey);
	List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>
	
    <%
    for(Entity device : devices){
    	%>
    	<table>
	    	<tr>
		    	<td>IMEI: <%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Device name: <%= device.getProperty(Constants.DEVICE_NAME_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td>GCM ID: <%= device.getProperty(Constants.DEVICE_GCM_ID_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td>SDK version: <%= device.getProperty(Constants.DEVICE_SDK_VERSION_COLUMN) %></td>
	    	</tr>
		    <tr>
		    	<td>Date: <%= device.getProperty(Constants.DEVICE_DATE_COLUMN) %></td>
	    	</tr>
    	 </table>
    	 <hr>
    	<%
    }
    %>
    
    		<h1>
				All Devices
			</h1>

<%
	// Run an ancestor query to ensure we see the most up-to-date
	// view of the Greetings belonging to the selected Guestbook.
	query = new Query(Constants.DEVICE_TABLE_NAME);
	devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>
	
    <%
    for(Entity device : devices){
    	%>
    	<table>
	    	<tr>
		    	<td>IMEI: <%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Device name: <%= device.getProperty(Constants.DEVICE_NAME_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td>GCM ID: <%= device.getProperty(Constants.DEVICE_GCM_ID_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td>SDK version: <%= device.getProperty(Constants.DEVICE_SDK_VERSION_COLUMN) %></td>
	    	</tr>
		    <tr>
		    	<td>Date: <%= device.getProperty(Constants.DEVICE_DATE_COLUMN) %></td>
	    	</tr>
    	 </table>
    	 <hr>
    	<%
    }
    %>

			<h1>
				Register own device
			</h1>

			</table>
			<form action="/registerdevice" method="post" class="register">
				<input type="number" name="<%= Constants.IMEI %>" id="<%= Constants.IMEI %>" placeholder="IMEI" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="text" name="<%= Constants.DEVICE_NAME %>" id="<%= Constants.DEVICE_NAME %>" placeholder="DEVICE NAME" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="text" name="<%= Constants.GCM_ID %>" id="<%= Constants.GCM_ID %>" placeholder="GCM ID" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="number" name="<%= Constants.SDK_VERSION %>" id="<%= Constants.SDK_VERSION %>" placeholder="SDK VERSION" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
				<input type="submit" value="Add device"/>
			</form>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />