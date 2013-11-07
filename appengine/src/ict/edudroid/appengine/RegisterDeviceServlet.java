package ict.edudroid.appengine;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class RegisterDeviceServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Entity registereddevice = new Entity("RegisteredDevice",req.getParameter("imei"));
		registereddevice.setProperty("gcm_id", req.getParameter("gcm_id"));
		registereddevice.setProperty("sdk_version", req.getParameter("sdk_version"));
		registereddevice.setProperty("cellular", req.getParameter("cellular"));
		registereddevice.setProperty("wifi", req.getParameter("wifi"));
		registereddevice.setProperty("bluetooth", req.getParameter("bluetooth"));
		registereddevice.setProperty("gps", req.getParameter("gps"));
		registereddevice.setProperty("date", new Date());
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(registereddevice);
	}
}
