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
        <title>Upload Module</title>
        <link rel="stylesheet" type="text/css" href="http://ictdroidlab.appspot.com/tablestyle.css" media="screen" />
    </head>
    <body>
    <h2>Upload Module</h2>
    <div class="CSSTableGenerator" >
    <table>
        <form action="<%= blobstoreService.createUploadUrl("/uploadModule") %>" method="post" enctype="multipart/form-data">
		<tr>
			<td>JAR file:</td>
			<td>	
				<input type="file" name="jarFile" accept=".jar">
			</td>
		</tr>
		<tr>
			<td>DESC file:</td>
			<td>	
				<input type="file" name="descFile" accept=".desc">
			</td>
		</tr>
        <tr>
        	<td>
            	<input type="submit" value="Submit">
            </td>
        </tr>
    	</form>
    </table>
    <div class="CSSTableGenerator" >
    </body>
</html>