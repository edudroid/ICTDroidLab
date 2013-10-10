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
    Query query = new Query("RegisteredDevice");
    List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Registered Devices</title>
        
    </head>
    <body>
    <h2>Registered Devices:</h2>
    <table border="1">
    	<tr>
    		<th>Imei</th><th>Sdk version</th><th>Cellular</th><th>Bluetooth</th><th>Wifi</th><th>Gps</th>
    	</tr>
			    <%
			    for(Entity device : devices){
			    	%> <tr><td> <%
			    	out.println(device.getKey().getName());%></td><td><%
			    	out.println(device.getProperty("sdk_version"));%></td><td><%
			    	out.println(device.getProperty("cellular"));%></td><td><%
			    	out.println(device.getProperty("bluetooth"));%></td><td><%
			    	out.println(device.getProperty("wifi"));%></td><td><%
			    	out.println(device.getProperty("gps"));%></td></tr>
			    	<%
			    }
			    %>
    </table>
    </body>
</html>