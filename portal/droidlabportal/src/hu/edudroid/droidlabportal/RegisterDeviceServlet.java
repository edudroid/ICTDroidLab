package hu.edudroid.droidlabportal;

import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class RegisterDeviceServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Can register device only in session
		User user = UserManager.checkUser(req.getSession(), req, resp);
		Key userKey = null;
		if (user != null) {
			userKey = user.getKey();
		}
		if (userKey==null) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		} 
		String imei = req.getParameter(Constants.IMEI);
		if (imei == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_IMEI);
			return;
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Filter imeiFilter = new FilterPredicate(Constants.DEVICE_IMEI_COLUMN, FilterOperator.EQUAL, imei);
	    
		Query query = new Query(Constants.DEVICE_TABLE_NAME, userKey).setFilter(imeiFilter);
		List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(1));
		if (devices.size() > 0) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_DEVICE_ALREADY_EXISTS);
			return;
		}
		// Add device
        Entity device = new Entity(Constants.DEVICE_TABLE_NAME, userKey);
        device.setProperty(Constants.DEVICE_IMEI_COLUMN, imei);
        datastore.put(device);
		if (req.getParameterMap().containsKey(Constants.WEB)) {
			resp.sendRedirect("/jsp/device.jsp?" + Constants.IMEI + "=" + imei);
		} else {
			resp.setContentType("text/plain");
			resp.getWriter().println("DEVICE_REGISTERED");
		}
	}
}