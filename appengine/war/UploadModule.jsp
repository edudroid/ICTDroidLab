<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>


<html>
    <head>
        <title>Upload Module</title>
    </head>
    <body>
    <table>
        <form action="<%= blobstoreService.createUploadUrl("/uploadModule") %>" method="post" enctype="multipart/form-data">
		<tr>
			<td>Module Author:</td>
			<td>	
				<input type="text" name="author">
			</td>
		</tr>
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
    </body>
</html>