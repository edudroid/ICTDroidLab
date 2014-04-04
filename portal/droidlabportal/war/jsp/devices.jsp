<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICES %>" />
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
	<jsp:param name="selected" value="<%=Constants.DEVICES %>" />
</jsp:include>

		<div class="main_content">
			<h1> My Devices </h1>

<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	Query query = new Query(Constants.DEVICE_TABLE_NAME,user.getKey());
	List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>
    <%
    for(Entity device : devices){
    	%>
	    	<a href="/device?IMEI=<%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %>">
	    		<%= device.getProperty(Constants.DEVICE_NAME_COLUMN) %> (<%= device.getProperty(Constants.DEVICE_IMEI_COLUMN) %>)
	    	</a>
    	<%
    }
    %>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />