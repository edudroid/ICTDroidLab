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
    Query query = new Query("Modules");
    query.addSort("date", SortDirection.DESCENDING);
    List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Uploaded Modules</title>
        <link rel="stylesheet" type="text/css" href="http://ictdroidlab.appspot.com/tablestyle.css" media="screen" />
    </head>
    <body>
    <h2>Uploaded Modules</h2>
    <div class="CSSTableGenerator" >
    <table border="1">
    	<tr>
    		<th>Modul name</th><th>Jar file</th><th>Class name</th><th>Date</th><th>User</th><th>Email</th>
    	</tr>
			    <%
			    for(Entity module : modules){
			    	%> <tr><td> <%
			    	out.println(module.getProperty("module_name"));%></td><td><%
			    	out.println(module.getProperty("jar_file"));%></td><td><%
			    	out.println(module.getProperty("class_name"));%></td><td><%
			    	out.println(module.getProperty("date"));%></td><td><%
			    	out.println(module.getProperty("nickname"));%></td><td><%
			    	out.println(module.getProperty("email"));%></td></tr>
			    	<%
			    }
			    %>
    </table>
    </div>
    </body>
</html>