<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@page import="com.google.appengine.api.datastore.Query.SortDirection"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions.Builder"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterPredicate"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.USERS %>" />
</jsp:include>
<%
User user = UserManager.checkUser(session, request, response);
if (user == null) {
	response.sendRedirect("/loginform");
	return;
}
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.USERS %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Users
			</h1>
<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query query = new Query(Constants.USER_TABLE_NAME);
	List<Entity> users = datastore.prepare(query).asList(Builder.withDefaults());
	
%>
	<table>
	<% for(Entity userX : users){ 
		if(!userX.getProperty(Constants.USER_EMAIL_COLUMN).equals(user.getEmail())){
	%>
		
		<tr>
			<td>Email: <%= userX.getProperty("email") %></td>
		</tr>
		<tr>
			<td>Registration: <%= userX.getProperty("reg_date") %></td>
		</tr>
		<tr>
			<form action="/changeuserrole" method="post">
			<td>			
				<select name="<%= Constants.ROLE %>" id="<%= Constants.ROLE %>">
				  <% if(userX.getProperty(Constants.USER_ROLE_COLUMN).equals(Constants.ROLE_USER)) {%>
				  <option value="<%= Constants.ROLE_USER %>" selected>User</option>
				  <%} else { %>
				  <option value="<%= Constants.ROLE_USER %>">User</option>
				  <%} %>
				  
				  <% if(userX.getProperty(Constants.USER_ROLE_COLUMN).equals(Constants.ROLE_RESEARCHER)) {%>
				  <option value="<%= Constants.ROLE_RESEARCHER %>" selected>Researcher</option>
				  <%} else { %>
				  <option value="<%= Constants.ROLE_RESEARCHER %>">Researcher</option>
				  <%} %>
				  
				  <% if(userX.getProperty(Constants.USER_ROLE_COLUMN).equals(Constants.ROLE_ADMIN)) {%>
				  <option value="<%= Constants.ROLE_ADMIN %>" selected>Admin</option>
				  <%} else { %>
				  <option value="<%= Constants.ROLE_ADMIN %>">Admin</option>
				  <%} %>
				</select>
			</td>
			<td>
				<input type="hidden" name="<%= Constants.EMAIL %>" id="<%= Constants.EMAIL %>" value="<%= userX.getProperty(Constants.USER_EMAIL_COLUMN) %>">
				<input type="hidden" name="<%= Constants.WEB %>" id="<%= Constants.WEB %>" value="true">
				<input type="submit" value="Change"/>
			</td>		
			</form>
		</tr>
	<% 
		}
	}%>
    </table>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />