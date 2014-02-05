<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%=Constants.DEVICE %>" />
</jsp:include>
<div id="adbox">
	<div class="clearfix">
		<img src="/images/box.png" alt="Img" height="342" width="368">
		<div>
			<h1>It's good to know!</h1>
			<h2>Create, deploy, evaluate.</h2>
			<p>
				DroidLab is an open crowdsourcing platform for mobile sensing. Create measurement tasks, deploy them on a large device pool, hang back and wait for the results! 
				<span><a href="index.html" class="btn">Sign me up!</a>
				<b>Don’t worry it’s for free</b></span>
			</p>
		</div>
	</div>
</div>
<div id="contents">
	<div id="tagline" class="clearfix">
		<h1>Design With Simplicity.</h1>
		<div>
			<p>
				You can replace all this text with your own text. Want an easier solution for a Free Website?
			</p>
			<p>
				Head straight to Wix and immediately start customizing your website!
			</p>
			<p>
				Wix is an online website builder with a simple drag & drop interface, meaning you do the work online and instantly publish to the web.
			</p>
		</div>
		<div>
			<p>
				You can replace all this text with your own text. Want an easier solution for a Free Website?
			</p>
			<p>
				Head straight to Wix and immediately start customizing your website!
			</p>
			<p>
				Wix is an online website builder with a simple drag & drop interface, meaning you do the work online and instantly publish to the web.
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />