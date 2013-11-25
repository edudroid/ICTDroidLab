<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>    

<%
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Greetings belonging to the selected Guestbook.
    Query query = new Query("Modules");
    List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
    query = new Query("RegisteredDevice");
    List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Module Selector</title>

	        <script type="text/javascript"> 
			
			function moduleSelected(Index) {
				document.getElementById('ModuleInfo').innerHTML = 'Selected: '+Index;
			} 
			function deviceSelected(Index) { 
				document.getElementById('DeviceInfo').innerHTML = 'Selected: '+Index;
			} 
			
			</script> 
        
    </head>
    <body>
    <h2>Module Selector</h2>
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
					<option value="<%=device.getProperty("gcm_id")%>"><%=device.getKey().getName()%></option>
						<%
						}
					%>
				</select>
			</td>
			<td>
            	<input type="submit" value="Submit">
            </td>
		</tr>
		<tr>
        	<td>
        		<p id='ModuleInfo'>ModuleInfos</p>
        	</td>
        	<td>
        		<p id='DeviceInfo'>DeviceInfos</p>
        	</td>
        </tr>
    	</form>
    </table>
    </body>
</html>