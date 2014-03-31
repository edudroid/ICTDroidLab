<%@page import="hu.edudroid.droidlabportal.user.UserManager"%>
<%@page import="hu.edudroid.droidlabportal.user.User"%>
<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.PROFILE %>" />
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
	<jsp:param name="selected" value="<%=Constants.USER_ROOT %>" />
</jsp:include>
		<div class="main_content">
			<h1>
				Welcome to your lab! Role: <%=user.getRole() %>
			</h1>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />