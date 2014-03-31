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
	<jsp:param name="selected" value="<%=Constants.PROFILE %>" />
</jsp:include>
<%
	String email = (String)session.getAttribute(Constants.EMAIL);
	if (email != null) {
%>
<div id="contents">
	<div id="tagline" class="clearfix">
<jsp:include page="/jsp/usersidemenu.jsp">
	<jsp:param name="selected" value="<%=Constants.PROFILE %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Profile
			</h1>
<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key userRootKey = KeyFactory.createKey(Constants.USER_TABLE_NAME, Constants.USER_ROOT);
	Query.Filter emailFilter = new FilterPredicate(Constants.USER_EMAIL_COLUMN, FilterOperator.EQUAL, email);
	Query query = new Query(Constants.USER_TABLE_NAME, userRootKey).setFilter(emailFilter);
	List<Entity> user = datastore.prepare(query).asList(Builder.withLimit(1));
	
%>
	<table>
		<tr>
			<td>Email: <%= user.get(0).getProperty("email") %></td>
		</tr>
		<tr>
			<td>Registration: <%= user.get(0).getProperty("reg_date") %></td>
		</tr>	
    </table>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />

<% } else {
	response.sendRedirect("/loginform");
}
%>