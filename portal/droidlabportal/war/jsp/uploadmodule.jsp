<%@page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@page import="com.google.appengine.api.blobstore.BlobstoreService"%>
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
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.UPLOADMODULE %>" />
</jsp:include>
<%
	String email = (String)session.getAttribute(Constants.EMAIL);
	if (email == null) {
		response.sendRedirect("/loginform");
		return;
	}
	Key userKey = null;
	try {
		userKey = (Key)session.getAttribute(Constants.USER_KEY);
	} catch (Exception e) {
		response.sendRedirect("/loginform");
		return;
	}
	if (userKey == null) {
		response.sendRedirect("/loginform");
		return;
	}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.UPLOADMODULE %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Upload module
			</h1>
<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	UserService userService = UserServiceFactory.getUserService();
	
	if(request.getParameter("succes")!=null && request.getParameter("succes").equals("true")){
		%><p>Upload succesful!</p> <%
	}
%>
			
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
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />