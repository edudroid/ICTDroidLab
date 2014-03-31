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
	<jsp:param name="selected" value="<%=Constants.MEASUREMENT %>" />
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
	<jsp:param name="selected" value="<%=Constants.MEASUREMENT %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Measurement
			</h1>
<%
    
	if(request.getParameter("succes")!=null && request.getParameter("succes").equals("true")){
		%><p>GCM has been sent successfully!</p> <%
	}
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    Query query = new Query(Constants.MODULES_TABLE_NAME,userKey);
	List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    query = new Query(Constants.DEVICE_TABLE_NAME,userKey);
    List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>
	<table>
        <form action="/GCMBroadcast" method="post">
		
		<th>Module Name</th><th>IMEI</th>
		<tr>
			<td>
				<select name="modules" onchange="moduleSelected(this.selectedIndex);">
					<option value="0">--Please Select--</option>
					<% for(Entity module : modules){ %>
					<option value="<%=module.getProperty("jarFileBlobKey")%>"><%=module.getProperty("module_name")%></option>
						<%
						}
					%>
				</select>
			</td>
			<td>	
				<select name="devices" onchange="deviceSelected(this.selectedIndex);">
					<option value="0">--Please Select--</option>
					<% for(Entity device : devices){ %>
					<option value="<%=device.getProperty("gcm_id")%>"><%=device.getProperty("device_name")%></option>
						<%
						}
					%>
				</select>
			</td>
			<td>
            	<input type="submit" value="Start Measuring">
            </td>
		</tr>
    	</form>
    </table>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />