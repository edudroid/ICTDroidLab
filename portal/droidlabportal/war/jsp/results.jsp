<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="java.util.Date"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@page import="com.google.appengine.api.datastore.Query.SortDirection"%>
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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.RESULTS %>" />
</jsp:include>
<%
	User user = UserManager.checkUser(session, request, response);
	if (user == null) {
		response.sendRedirect("/loginform");
		return;
	}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.RESULTS %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Results
			</h1>
<%
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
	Query query = new Query(Constants.DEVICE_TABLE_NAME);
	List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	
	for(Entity device : devices){
		query = new Query(Constants.RESULTS_TABLE_NAME,device.getKey()).addSort(Constants.RESULTS_DATE_COLUMN, SortDirection.DESCENDING);
	    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
	    
	    if(results.size()>0){
	    %><p>Results for: <%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %></p>
	    
			<table>
				<tr><th>Date</th><th>Level</th><th>Module</th><th>Message</th></tr>
			<%
			for(Entity result : results){
				String dateString = null;
				try {
					dateString = Constants.formatTime(new Date(Long.parseLong((String)result.getProperty(Constants.RESULTS_DATE_COLUMN))));
				} catch (Exception e){
					e.printStackTrace();
					dateString = "N/A";
				}
			%>
				<tr>
					<td><%= dateString %></td>
					<td><%= result.getProperty(Constants.RESULTS_LOG_LEVEL_COLUMN) %></td>
					<td><%= result.getProperty(Constants.RESULTS_MODULE_NAME_COLUMN) %></td>
					<td><%= result.getProperty(Constants.RESULTS_MESSAGE_COLUMN) %></td>
				</tr>
			<%
			}	
			%></table><%
	    }
	    else{
	    	%><p>No results found for IMEI: <%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %></p><%
	    }
	} %>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />