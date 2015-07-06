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
		<div>
			A DroidLab keretrendszert azért hoztuk létre, mert az okostelefonokra irányuló kutatásaink során egyre gyakrabban szembesültünk azzal, hogy ugyanazokat a feladatokat implementáljuk újra és újra.<br/>
			A mérőalkalmazások funkcionalitása, és ennek megfelelően az implementációk és az infrastruktúra nagymértékben átfedték egymást. 
		</div>
		<div>
			<img src="/images/architecture.png" alt="Img">
			<h2>A keretrendszer felépítése</h2>
			<p>
				A DroidLab két fő elemből áll, az AppEngine felhőben futó szerver komponenséből és a felhasználók eszközeire telepített kliensalkalmazásból.
			</p>
			<h3>DroidLab kliens</h3>
			<p>
				A DroidLab kliens több Android alkalmazásból áll. Ez a megközelítés teszi lehetővé, hogy a felhasználók a DroidLab telepítésüket saját preferenciáikhoz és az eszköz képességeihez szabhassák.<br/>
				A felhasználók a DroidLab core Android alkalmazáson keresztül férhetnek hozzá a DroidLab-hoz, a többi alkalmazás csak háttér-szolgáltatásokat biztosít a mérésekhez.
				A core alkalmazás lehetőséget biztosít a felhasználóknak a mérések engedélyezésére vagy visszautasítására, a mérések adatainak megtekintésére.
				Itt állíthatják be a környezet számára rendelkezésére bocsájtott erőforrások is.
			</p>
			<h3>DroidLab portál</h3>
			<p>
				A rendszer használatához a felhasználónak regisztrálnia kell a <a href="droidlabportal.appspot.com" target="_blank">DroidLab portálon</a>.
				A portál felhasználói át tudják tekinteni az eszközeik aktivitását, futtatott méréseket, feltöltött mérési adatokat.<br/>
				A kutatói szerepkörrel rendelkező felhasználók ezen felül méréseket ütemezhetnek a többi felhasználó eszközeire, és megkapják a méréseik kimeneteit.
			</p>
		</div>
		<div>
			<img src="/images/box-of-icons.png" alt="Img">
			<h2>Modularitás</h2>
			<p>
				A Droidlab igazodik a felhasználói elvárásokhoz és a heterogén eszközpark lehetőségeihez: a különböző funkciókat külön letölthető egységek implementálják, így minden felhasználó testreszabhatja, hogy pontosan milyen adatokhoz enged hozzáférést.
			</p>
			<h2>Lokális feldolgozás</h2>
			<p>
				Sok feladat lényegesen egyszerűbben valósítható meg a mérőeszközön, mint később, az összegyűjtött adatokon. Így nem csak bonyolult adatbányászati feladatok valósíthatóak meg egyszerű vezérlési szerkezetekkel a mérőeszközön, de a forgalmazott adatmennyiség is csökken. Például ha egy autósnak csak a napi maximális sebességére kiváncsi a kutató, utólagos feldolgozás esetén másodperces bontásban szüksége van a sebességadatra. A DroidLab használata esetén a 86.400 mérési adat helyett elég egyetlen egyet feltölteni.
			</p>
		</div>
	</div>
</div>
<jsp:include page="/jsp/footer.jsp" />