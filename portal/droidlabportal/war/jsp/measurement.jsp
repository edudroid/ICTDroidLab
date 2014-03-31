<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
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
	<jsp:param name="selected" value="<%=Constants.MEASUREMENT %>" />
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
    
    Query query = new Query(Constants.MODULES_TABLE_NAME,user.getKey());
	List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    query = new Query(Constants.DEVICE_TABLE_NAME,user.getKey());
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