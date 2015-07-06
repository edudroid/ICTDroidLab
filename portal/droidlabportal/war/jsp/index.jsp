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
<div id="floatingbox">
	<div class="clearfix">
		<img src="/images/box.png" alt="Img" height="342" width="368">
		<div>
			<h1>DroidLab</h1>
			<h2>Közösségi érzékelés egyszerűen.</h2>
			<p>
				A DroidLab keretrendszer segítségével mérő alkalmazások telepíthetőek a résztvevő készülékekre, nyomon követhető a futás, és elérhetőek a végeredmények.
				Csatlakozzon kutatóként vagy résztvevőként a DroidLab-hoz! 
				<span><a href="/regform" class="btn">Regisztráció</a>
				<b>Nyílt béta</b></span>
			</p>
		</div>
	</div>
</div>	
<div id="contents-under-adbox">
	<div id="tagline" class="clearfix">
		<h1>A DroidLab-ról dióhéjban</h1>
		<div>
			<p>
				A DroidLab keretrendszer olyan kutatóknak készül, akik Android eszközök szélesebb körén szeretnének méréseket végezni.
				A keretrendszert úgy készítettük el, hogy azok számára is elérhetővé tegye az Android platform lehetőségeit, akik nem jártasak annak programozásában.
			</p>
			<p>
				A keretrendszer leveszi a kutatók válláról az Android alkalmazás fejlesztésének, terjesztésének terhét, így számukra az egyetlen feladat a mérő-alkalmazás elkészítése.
				A keretrendszer gondoskodik a mérőkód terjesztéséről, futtatásáról és a generált kimenet összegyűjtéséről.
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />