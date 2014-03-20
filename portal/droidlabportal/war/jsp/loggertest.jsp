<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
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
		<div>
			<h1>
				Log testing
			</h1>

			<table>
			<form action="/uploadLog" method="post" class="register">
				<input type="hidden" name="<%= Constants.LOG_COUNT %>" id="<%= Constants.LOG_COUNT %>" value="2"/>
				<tr>
				<td>
				<input type="text" name="0 module" id="0 module" placeholder="Module" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="0 log_level" id="0 log_level" placeholder="LOG level" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="0 date" id="0 date" placeholder="Date" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="0 message" id="0 message" placeholder="Message" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				
				<tr>
				<td>
				<tr>
				<td>
				<input type="text" name="1 module" id="1 module" placeholder="Module" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="1 log_level" id="1 log_level" placeholder="LOG level" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="1 date" id="1 date" placeholder="Date" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="text" name="1 message" id="1 message" placeholder="Message" onFocus="this.select();" onMouseOut="javascript:return false;"/>
				</td>
				</tr>
				<tr>
				<td>
				<input type="submit" value="Upload logs"/>
				</td>
				</tr>
			</table>
			</form>
			
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />