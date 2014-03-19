<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@page import="com.google.appengine.api.datastore.Query.SortDirection"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
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
	<jsp:param name="selected" value="<%=Constants.MODULES %>" />
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
	<jsp:param name="selected" value="<%=Constants.MODULES %>" />
</jsp:include>
		<div>
			<h1>
				Modules
			</h1>
<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	// Run an ancestor query to ensure we see the most up-to-date
	// view of the Greetings belonging to the selected Guestbook.
	Query query = new Query(Constants.MODULES_TABLE_NAME,userKey);
	List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>
	
    <%
    for(Entity module : modules){
    	%>
    	<table>
	    	<tr>
		    	<td>ID: <%= module.getProperty(Constants.MODULES_MODULE_ID_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Module name: <%= module.getProperty(Constants.MODULES_MODULE_NAME_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td><%= module.getProperty(Constants.MODULES_CLASS_NAME_COLUMN) %></td>
		    </tr>
		    <tr>
		    	<td>Author: <%= module.getProperty(Constants.MODULES_AUTHOR_COLUMN) %></td>
	    	</tr>
		    <tr>
		    	<td>Date: <%= module.getProperty(Constants.MODULES_DATE_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Measurement length: <%= module.getProperty(Constants.MODULES_MEASUREMENT_LENGTH_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Permissions: <ul>
	    	<%
	    	List<String> list=(List<String>)module.getProperty(Constants.MODULES_PERMISSIONS_COLUMN); 
	    	for(int i=0;i<list.size();i++){
	    		%> <li><%= list.get(i) %></li> <%
	    	}
	    	%>
	    			</ul>
	    		</td>
	    	</tr>
	    	<tr>
		    	<td>Used plugins: <ul>
	    	<%
	    	list=(List<String>)module.getProperty(Constants.MODULES_USED_PLUGINS_COLUMN); 
	    	for(int i=0;i<list.size();i++){
	    		%> <li><%= list.get(i) %></li> <%
	    	}
	    	%>
	    			</ul>
	    		</td>
	    	</tr>
	    	<tr>
		    	<td>Quotas: <%= module.getProperty(Constants.MODULES_QUOTAS_COLUMN) %></td>
	    	</tr>
	    	<tr>
		    	<td>Website: <%= module.getProperty(Constants.MODULES_WEBSITE_COLUMN) %></td>
	    	</tr>
    	 </table>
    	 <hr>
    	<%
    }
    %>
   
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />