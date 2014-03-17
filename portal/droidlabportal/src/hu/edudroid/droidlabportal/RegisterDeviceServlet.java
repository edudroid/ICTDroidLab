package hu.edudroid.droidlabportal;

import java.io.IOException;
import java.util.Date;
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
		Key userKey = null;
		try {
			userKey = (Key)req.getSession().getAttribute(Constants.USER_KEY);
		} catch (Exception e) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		}
		if (userKey == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		}
		String imei = req.getParameter(Constants.DEVICES_IMEI_COLUMN);
		if (imei == null) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_IMEI);
			return;
		}
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Filter imeiFilter = new FilterPredicate(Constants.DEVICES_TABLE_NAME, FilterOperator.EQUAL, imei);
	    
		Query query = new Query(Constants.DEVICES_TABLE_NAME, userKey).setFilter(imeiFilter);
		List<Entity> devices = datastore.prepare(query).asList(Builder.withLimit(1));
		if (devices.size() > 0) {
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_DEVICE_ALREADY_EXISTS);
			return;
		}
		// Add device
        Entity device = new Entity(Constants.MYDEVICE_TABLE_NAME, userKey);
        device.setProperty(Constants.DEVICES_IMEI_COLUMN, imei);
        /*
        device.setProperty(Constants.DEVICES_SDK_COLUMN, req.getParameter(Constants.DEVICES_SDK_COLUMN));
        device.setProperty(Constants.DEVICES_CELLULAR_COLUMN, req.getParameter(Constants.DEVICES_CELLULAR_COLUMN));
        device.setProperty(Constants.DEVICES_WIFI_COLUMN, req.getParameter(Constants.DEVICES_WIFI_COLUMN));
        device.setProperty(Constants.DEVICES_GPS_COLUMN, req.getParameter(Constants.DEVICES_GPS_COLUMN));
        device.setProperty(Constants.DEVICES_BLUETOOTH_COLUMN, req.getParameter(Constants.DEVICES_BLUETOOTH_COLUMN));
        */
        device.setProperty(Constants.DEVICES_DATE_COLUMN, new Date());
        datastore.put(device);
		if (req.getParameterMap().containsKey(Constants.WEB)) {
			resp.sendRedirect("/jsp/device.jsp?" + Constants.IMEI + "=" + imei);
		} else {
			resp.setContentType("text/plain");
			resp.getWriter().println("DEVICE_REGISTERED");
		}
	}
}