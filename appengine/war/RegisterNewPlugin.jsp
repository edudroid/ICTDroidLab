<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	UserService userService = UserServiceFactory.getUserService();
%>


<html>
    <head>
        <title>Register Plugin</title>
    </head>
    <body>
    <table>
        <form action="/registerPlugin" method="post">
		<tr>
			<td>Plugin name:</td>
			<td>	
				<input type="text" name="plugin_name">
			</td>
		</tr>
		<tr>
			<td>Plugin class-name:</td>
			<td>	
				<input type="text" name="plugin_class">
			</td>
		</tr>
		<tr>
			<td>Plugin version:</td>
			<td>	
				<input type="number" name="plugin_version">
			</td>
		</tr>
		<tr>
			<td>Plugin description:</td>
			<td>	
				<input type="text" name="plugin_desc">
			</td>
		</tr>
        <tr>
        	<td>
            	<input type="submit" value="Submit">
            </td>
        </tr>
    	</form>
    </table>
    </body>
</html>