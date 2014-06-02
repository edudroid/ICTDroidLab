<%@page import="com.google.appengine.api.datastore.Query.SortDirection"%>
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
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	Logger log = Logger.getLogger("device.jsp");
	User user = UserManager.checkUser(session, request, response);
	if (user == null) {
		%>
		{"error":"not logged in"}
		<%
		return;
	}
	String moduleName = (String)request.getParameter(Constants.MODULE_NAME);
	if (moduleName == null) {
		%>
		{"error":"no module name specified"}
		<%
		return;
	} else {
		log.info("Module name available");
	}
	String imei = (String)request.getParameter(Constants.IMEI);
	// Check if there is a device
	Entity selectedDevice = null;
	if (imei == null) {
		%>
		{"error":"no imei specified"}
		<%
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
		%>
		{"error":"device not found"}
		<%
		return;
	}
%>
{"device": {"name":"<%= selectedDevice.getProperty(Constants.DEVICE_NAME_COLUMN) %>",
"imei":"<%= selectedDevice.getProperty(Constants.DEVICE_IMEI_COLUMN) %>",
"sdk":"<%= selectedDevice.getProperty(Constants.DEVICE_SDK_VERSION_COLUMN) %>",
"registered":"<%= Constants.formatDate((Date)selectedDevice.getProperty(Constants.DEVICE_DATE_COLUMN)) %>"
},
"module":"<%= moduleName %>",
<%
	Query.Filter moduleFilter = new FilterPredicate(Constants.RESULTS_MODULE_NAME_COLUMN, FilterOperator.EQUAL, moduleName);
	query = new Query(Constants.RESULTS_TABLE_NAME, selectedDevice.getKey()).setFilter(moduleFilter).addSort(Constants.RESULTS_DATE_COLUMN, SortDirection.DESCENDING);
	List<Entity> results = datastore.prepare(query).asList(Builder.withLimit(10));
%>
"data":[
<%
	boolean first = true;
	for(Entity result : results){
		String dateString = null;
		try {
			dateString = Constants.formatTime(new Date(Long.parseLong((String)result.getProperty(Constants.RESULTS_DATE_COLUMN))));
		} catch (Exception e){
			e.printStackTrace();
			dateString = "N/A";
		}
		if (!first) {
			%> , <%
		}
		first = false;
%>
	{"date":"<%= dateString %>",
	"log_level":"<%= result.getProperty(Constants.RESULTS_LOG_LEVEL_COLUMN) %>",
	"result":"<%= result.getProperty(Constants.RESULTS_MESSAGE_COLUMN) %>"}
<%
}
%>
]
}