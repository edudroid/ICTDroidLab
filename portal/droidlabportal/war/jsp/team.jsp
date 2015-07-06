<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.TEAM %>" />
</jsp:include>
	<div id="contents">
		<div class="features">
			<h1>Csapat</h1>
			<p>
				A projektben az oktatók és tanszéki alkalmazottak mellett több hallgató is részt vett, részt vesz.
			</p>
			<div>
				<img src="/images/team/vida.gif" alt="Img">
				<h3>Vida Rolland</h3>
				<p>
					Projekt manager és vezető kutató, a szenzorhálózatok és elosztott rendszerek szakértője.
				</p>
			</div>			
			<div>
				<img src="/images/team/feher.gif" alt="Img">
				<h3>Fehér Gábor</h3>
				<p>
					Fejlesztést vezető oktató
				</p>
			</div>			
			<div>
				<img src="/images/team/mate.gif" alt="Img">
				<h3>Máté Miklós</h3>
				<p>
					Java fejlesztő, backend
				</p>
			</div>
			<div>
				<img src="/images/team/lajtha.gif" alt="Img">
				<h3>Lajtha Balázs</h3>
				<p>
					Android fejlesztő, frontend
				</p>
			</div>			
			<p>
				A témában Nagy Szabolcs és Nagy László készítettek diplomamunkát, Weisz Patrik védett szakdolgozatot, és Száray Bálint készít jelenleg szakdolgozatot. 
			</p>
		</div>
	</div>
<jsp:include page="/jsp/footer.jsp" />