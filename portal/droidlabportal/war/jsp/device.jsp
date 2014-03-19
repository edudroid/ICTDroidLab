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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICE %>" />
</jsp:include>
<%
	Logger log = Logger.getLogger("device.jsp");
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
	String imei = (String)request.getParameter(Constants.IMEI);
	Entity selectedDevice = null;
	if (imei == null) {
		log.warning("No IMEI");
		response.sendRedirect("/loginform");
		return;
	}
	// Find device by imei
	// Check for password in datastore
	/*
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query.Filter imeiFilter = new FilterPredicate(Constants.DEVICES_IMEI_COLUMN, FilterOperator.EQUAL, imei);
	Query query = new Query(Constants.DEVICES_TABLE_NAME, userKey).setFilter(imeiFilter);
	List<Entity> devices = datastore.prepare(query).asList(Builder.withDefaults());
	if (devices.size() == 1) {
		log.info("Device found");
		selectedDevice = devices.get(0);
	} else {
		log.warning("Device not found");
		response.sendRedirect("/devices");
		return;
	}
	*/
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICES %>" />
</jsp:include>
		<div>
			<h1>
				Device <%= selectedDevice.getProperty(Constants.DEVICE_IMEI_COLUMN) %>
			</h1>
	<% /*		
	<table>
		<tr>
			<td>IMEI: <%= devices.get(0).getProperty(Constants.DEVICE_IMEI_COLUMN) </td>
		</tr>
		<tr>
			<td>SDK version: <%= devices.get(0).getProperty(Constants.DEVICE_SDK_VERSION_COLUMN) </td>
		</tr>
		<tr>
			<td>CELLULAR: <%= devices.get(0).getProperty(Constants.DEVICE_CELLULAR_COLUMN) </td>
		</tr>
		<tr>
			<td>WIFI: <%= devices.get(0).getProperty(Constants.DEVICE_WIFI_COLUMN) </td>
		</tr>
		<tr>
			<td>GPS: <%= devices.get(0).getProperty(Constants.DEVICE_GPS_COLUMN) </td>
		</tr>
		<tr>
			<td>BLUETOOTH: <%= devices.get(0).getProperty(Constants.DEVICE_BLUETOOTH_COLUMN) </td>
		</tr>
		<tr>
			<td>Registration: <%= devices.get(0).getProperty(Constants.DEVICE_DATE_COLUMN) </td>
		</tr>
    </table>
    */ %>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />