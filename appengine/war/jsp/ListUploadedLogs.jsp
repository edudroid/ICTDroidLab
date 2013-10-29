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
    Query query = new Query("Logs");
    query.addSort("date");
    List<Entity> logs = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Uploaded Logs</title>
        
    </head>
    <body>
    <h2>Uploaded logs:</h2>
    <table border="1">
    	
			    <%
			    for(Entity log : logs){
			    	%>
			    	<tr>
			    	<td><a href='http://ictdroidlab.appspot.com/serveLog?blob-key=<%= log.getProperty("logFileBlobKey") %>'>IMEI: <%= log.getProperty("imei") %> - DATE: <%= log.getProperty("date") %></a></td>
			    	</tr>
			    	<%
			    }
			    %>
    </table>
    </body>
</html>