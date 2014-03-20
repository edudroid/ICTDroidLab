package hu.edudroid.droidlabportal;

import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class RegisterDeviceServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(RegisterDeviceServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Can register device only if user is present
		User user = UserManager.checkUser(req.getSession(), req, resp);
		Key userKey = user!=null?user.getKey():null;
		if (userKey==null) {
			log.warning("User not logged in");
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		}
		// Check if every field is present
		String imei = req.getParameter(Constants.IMEI);
		if (imei == null) {
			log.warning("No " + Constants.IMEI + " present");
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_IMEI);
			return;
		}
		String name = req.getParameter(Constants.DEVICE_NAME);
		if (name == null) {
			log.warning("No " + Constants.DEVICE_NAME + " present");
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_DEVICE_NAME);
			return;
		}
		String gcmId = req.getParameter(Constants.GCM_ID);
		if (gcmId == null) {
			log.warning("No " + Constants.GCM_ID + " present");
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_GCM_ID);
			return;
		}
		String sdkVersion = req.getParameter(Constants.SDK_VERSION);
		if (sdkVersion == null) {
			log.warning("No " + Constants.SDK_VERSION + " present");
			resp.setContentType("text/plain");
			resp.getWriter().println(Constants.ERROR_MISSING_SDK_VERSION);
			return;
		}
		DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
		Filter deviceFilter = new FilterPredicate(Constants.DEVICE_IMEI_COLUMN, FilterOperator.EQUAL, imei);
		Query query = new Query(Constants.DEVICE_TABLE_NAME).setFilter(deviceFilter);
		List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		Entity device = null;
		String message;
		if (devices.size() > 0) {
			// Update device
	        device = devices.get(0);
			log.info("Updating device.");
	        message = "DEVICE_UPDATED";
		} else {
			// Add device
			log.info("Add new device.");
			device = new Entity(Constants.DEVICE_TABLE_NAME, userKey);
			message = "DEVICE_REGISTERED";
		}
        device.setProperty(Constants.DEVICE_IMEI_COLUMN, imei);
        device.setProperty(Constants.DEVICE_NAME_COLUMN, name);
        device.setProperty(Constants.DEVICE_GCM_ID_COLUMN, gcmId);
        device.setProperty(Constants.DEVICE_SDK_VERSION_COLUMN, sdkVersion);
        device.setProperty(Constants.DEVICE_DATE_COLUMN, new Date());
        // TODO save plugin versions
        datastore.put(device);
        // Add device to session
        req.getSession().setAttribute(Constants.DEVICE_KEY, device.getKey());
        req.getSession().setAttribute(Constants.DEVICE_IMEI_KEY, imei);        
		resp.setContentType("text/plain");
		resp.getWriter().println(message);
	}
}