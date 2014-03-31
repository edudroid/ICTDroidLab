<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
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
    
	Query query = new Query(Constants.DEVICE_TABLE_NAME,user.getKey());
	List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	
	for(Entity device : devices){
		query = new Query(Constants.RESULTS_TABLE_NAME,device.getKey());
	    List<Entity> results = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	    
	    %><p>Results for: <%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %></p><%
	    
	    for(Entity result : results){
	    	%>
	    	<table>
		    	<tr>
			    	<td>Module name: <%= result.getProperty(Constants.RESULTS_MODULE_NAME_COLUMN) %></td>
			    </tr>
			    <tr>
			    	<td>Log level: <%= result.getProperty(Constants.RESULTS_LOG_LEVEL_COLUMN) %></td>
			    </tr>
			    <tr>
			    	<td>Date: <%= result.getProperty(Constants.RESULTS_DATE_COLUMN) %></td>
		    	</tr>
			    <tr>
			    	<td>Message: <%= result.getProperty(Constants.RESULTS_MESSAGE_COLUMN) %></td>
		    	</tr>
	    	 </table>
	    	 <p></p>
	    	<%
	    }	
	}
    %>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />