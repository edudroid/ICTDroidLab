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
    Query query = new Query("RegisteredPlugin");
    List<Entity> plugins = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Registered Plugins</title>
        <link rel="stylesheet" type="text/css" href="http://ictdroidlab.appspot.com/tablestyle.css" media="screen" />
    </head>
    <body>
    <h2>Registered Plugins</h2>
    <table>
    	<tr>
    		<th>Name</th><th>Class</th><th>Ver.</th><th>Descr.</th><th>Date</th>
    	</tr>
			    <%
			    for(Entity plugin : plugins){
			    	%> <tr><td> <%
			    	out.println(plugin.getProperty("name"));%></td><td><%
			    	out.println(plugin.getProperty("class"));%></td><td><%
			    	out.println(plugin.getProperty("version"));%></td><td><%
			    	out.println(plugin.getProperty("desc"));%></td><td><%
			    	out.println(plugin.getProperty("date"));%></td></tr>
			    	<%
			    }
			    %>
    </table>
    </div>
    </body>
</html>