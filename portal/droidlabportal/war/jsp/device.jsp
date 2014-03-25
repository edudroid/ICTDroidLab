<%@page import="java.util.Date"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.logging.Logger"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="java.util.List" %>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICE %>" />
</jsp:include>
<%
	Logger log = Logger.getLogger("device.jsp");
	User user = UserManager.checkUser(session, request, response);
	if (user == null) {
		response.sendRedirect("/loginform");
		return;
	}
	String imei = (String)request.getParameter(Constants.IMEI);
	// Check if there is a device
	Entity selectedDevice = null;
	if (imei == null) {
		log.warning("No IMEI");
		response.sendRedirect("/loginform");
		return;
	} else {
		log.info("IMEI available");
	}
	// Find device by imei
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query.Filter imeiFilter = new FilterPredicate(Constants.DEVICE_IMEI_COLUMN, FilterOperator.EQUAL, imei);
	Query query = new Query(Constants.DEVICE_TABLE_NAME).setFilter(imeiFilter); // TODO later on add ancestor to restrict user to own devices
	List<Entity> devices = datastore.prepare(query).asList(Builder.withDefaults());
	if (devices.size() == 1) {
		log.info("Device found");
		selectedDevice = devices.get(0);
	} else {
		log.warning("Device not found");
		response.sendRedirect("/devices");
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
				<%= selectedDevice.getProperty(Constants.DEVICE_NAME_COLUMN) %>
			</h1>
			<p>
				<span>IMEI: <%= selectedDevice.getProperty(Constants.DEVICE_IMEI_COLUMN) %></span><br/>
				<span>SDK version: <%= selectedDevice.getProperty(Constants.DEVICE_SDK_VERSION_COLUMN) %> </span><br/>
				<span>Registration: <%= Constants.formatDate((Date)selectedDevice.getProperty(Constants.DEVICE_DATE_COLUMN)) %> </span>
		    </p>
		</div>
	</div>
	<div>
		<h1>
			Results
		</h1>
<%
	query = new Query(Constants.RESULTS_TABLE_NAME, selectedDevice.getKey());
	List<Entity> results = datastore.prepare(query).asList(Builder.withDefaults());
%>
   
		<p>Results for:<%= selectedDevice.getProperty(Constants.DEVICE_IMEI_COLUMN) %></p>
		<table>
			<tr><th>Date</th><th>Level</th><th>Module</th><th>Message</th></tr>
<%
	for(Entity result : results){
%>
			<tr>
				<td><%= Constants.formatDate((Date)result.getProperty(Constants.RESULTS_DATE_COLUMN)) %></td>
				<td><%= result.getProperty(Constants.RESULTS_LOG_LEVEL_COLUMN) %></td>
				<td><%= result.getProperty(Constants.RESULTS_MODULE_NAME_COLUMN) %></td>
				<td><%= result.getProperty(Constants.RESULTS_MESSAGE_COLUMN) %></td>
			</tr>
<%
}	
%>
		</table>			
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />