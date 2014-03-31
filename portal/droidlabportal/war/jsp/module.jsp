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
	<jsp:param name="selected" value="<%=Constants.MODULE %>" />
</jsp:include>
<%
	Logger log = Logger.getLogger("module.jsp");
	User user = UserManager.checkUser(session, request, response);
	if (user == null) {
		response.sendRedirect("/loginform");
		return;
	}
	String module = (String)request.getParameter(Constants.MODULE);
	Entity selectedModule = null;
	if (module == null) {
		log.warning("No Module ID");
		response.sendRedirect("/loginform");
		return;
	} else {
		log.info("Module available: "+module);
	}
	// Find module by module id
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query.Filter moduleFilter = new FilterPredicate(Constants.MODULES_MODULE_ID_COLUMN, FilterOperator.EQUAL, module);
	Query query = new Query(Constants.MODULES_TABLE_NAME).setFilter(moduleFilter); // TODO later on add ancestor to restrict user to own devices
	List<Entity> modules = datastore.prepare(query).asList(Builder.withDefaults());
	if (modules.size() == 1) {
		log.info("Module found");
		selectedModule = modules.get(0);
	} else {
		log.warning("Module not found");
		response.sendRedirect("/modules");
		return;
	}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.MODULES %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				<%= selectedModule.getProperty(Constants.MODULES_MODULE_NAME_COLUMN) %>
			</h1>
			<table>
				<tr>
			    	<td>ID: <%= selectedModule.getProperty(Constants.MODULES_MODULE_ID_COLUMN) %></td>
		    	</tr>
		    	<tr>
			    	<td>Module name: <%= selectedModule.getProperty(Constants.MODULES_MODULE_NAME_COLUMN) %></td>
			    </tr>
			    <tr>
			    	<td><%= selectedModule.getProperty(Constants.MODULES_CLASS_NAME_COLUMN) %></td>
			    </tr>
			    <tr>
			    	<td>Author: <%= selectedModule.getProperty(Constants.MODULES_AUTHOR_COLUMN) %></td>
		    	</tr>
			    <tr>
			    	<td>Date: <%= selectedModule.getProperty(Constants.MODULES_DATE_COLUMN) %></td>
		    	</tr>
		    	<tr>
			    	<td>Measurement length: <%= selectedModule.getProperty(Constants.MODULES_MEASUREMENT_LENGTH_COLUMN) %></td>
		    	</tr>
		    	<tr>
			    	<td>Permissions: <ul>
		    	<%
		    	List<String> list=(List<String>)selectedModule.getProperty(Constants.MODULES_PERMISSIONS_COLUMN); 
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
		    	list=(List<String>)selectedModule.getProperty(Constants.MODULES_USED_PLUGINS_COLUMN); 
		    	for(int i=0;i<list.size();i++){
		    		%> <li><%= list.get(i) %></li> <%
		    	}
		    	%>
		    			</ul>
		    		</td>
		    	</tr>
		    	<tr>
			    	<td>Quotas: <%= selectedModule.getProperty(Constants.MODULES_QUOTAS_COLUMN) %></td>
		    	</tr>
		    	<tr>
			    	<td>Website: <%= selectedModule.getProperty(Constants.MODULES_WEBSITE_COLUMN) %></td>
		    	</tr>
		    </table>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />