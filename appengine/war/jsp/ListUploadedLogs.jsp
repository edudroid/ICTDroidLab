<%@page import="com.google.appengine.api.datastore.Query.SortDirection"%>
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
    query.addSort("date",SortDirection.DESCENDING);
    List<Entity> logs = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Uploaded Logs</title>
        <link rel="stylesheet" type="text/css" href="http://ictdroidlab.appspot.com/tablestyle.css" media="screen" />
    </head>
    <body>
    <h2>Uploaded Logs</h2>
    <table>
    	<th>IMEI</th><th>Date</th><th>-></th>
			    <%
			    for(Entity log : logs){
			    	%>
			    	<tr>
			    	<td><%= log.getProperty("imei") %></td>
			    	<td><%= log.getProperty("date") %></td>
			    	<td><a href='http://ictdroidlab.appspot.com/serveLog?blob-key=<%= log.getProperty("logFileBlobKey") %>'>Download</a></td>
			    	</tr>
			    	<%
			    }
			    %>
    </table>
    </div>
    </body>
</html>