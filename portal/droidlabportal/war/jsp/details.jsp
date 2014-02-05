<%@page import="hu.edudroid.droidlabportal.Constants"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="/jsp/header.jsp">
	<jsp:param name="selected" value="<%= Constants.FEATURES %>" />
</jsp:include>
<div id="contents">
	<div class="features">
		<h1>Features</h1>
		<p>
			Change, add, and remove items as you like. If you're having problems editing this website template, then don't hesitate to ask for help on the Forums.
		</p>
		<div>
			<img src="images/recycle.png" alt="Img">
			<h2>Customizable and Easy</h2>
			<p>
				You can replace all this text with your own text. Want an easier solution for a Free Website? Head straight to Wix and immediately start customizing your website!
			</p>
			<h2>Online in an Instant</h2>
			<p>
				Wix is an online website builder with a simple drag & drop interface, meaning you do the work online and instantly publish to the web. All Wix templates are fully customizable and free to use. Just pick one you like, click Edit, and enter the online editor.
			</p>
		</div>
		<div>
			<img src="images/box-of-icons.png" alt="Img">
			<h2>More Design Elements</h2>
			<p>
				Wix also offers a ton of free design elements right inside the editor, like images, icons, galleries, videos and large selection of Add Ons and social feeds.
			</p>
			<h2>Free to Use</h2>
			<p>
				Publish your Free Website in minutes! You can remove any link to our website from this website template, you're free to use this website template without linking back to us.
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />