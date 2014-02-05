<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/jsp/header.jsp"> 
	<jsp:param name="selected" value="<%= Constants.INDEX %>" />
</jsp:include>
<div id="adbox">
	<div class="clearfix">
		<img src="/images/box.png" alt="Img" height="342" width="368">
		<div>
			<h1>It's easy</h1>
			<h2>Create, deploy, evaluate.</h2>
			<p>
				DroidLab is an open crowdsourcing platform for mobile sensing. Create measurement tasks, deploy them on a large device pool, hang back and wait for the results! 
				<span><a href="/portal" class="btn">Sign me up!</a>
				<b>Don’t worry it’s for free</b></span>
			</p>
		</div>
	</div>
</div>	
<div id="contents-under-adbox">
	<div id="tagline" class="clearfix">
		<h1>Crowdsourcing seamlessly</h1>
		<div>
			<p>
				Crowdsourcing has never been that easy. You only have to write the business logic, everything else is taken care of.
			</p>
			<p>
				Write measurement code in plain Java. Start with a sample module and add calls to our plugin interfaces.
			</p>
			<p>
				Upload the code to our portal, select the required resources, and you're set.
			</p>
		</div>
		<div>
			<p>
				Devices are chosen for your measurement based on your resource needs.
			</p>
			<p>
				Data is uploaded periodically, you don't have to wait till the end of the measurement to start processing.
			</p>
			<p>
				Dozens of devices available to fulfill your curiosity.
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />