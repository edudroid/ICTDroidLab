package ict.edudroid.appengine;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class RegisterDeviceServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		Entity registereddevice = new Entity("RegisteredDevice",req.getParameter("imei"));
		registereddevice.setProperty("device_id", req.getParameter("device_id"));
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(registereddevice);
	}
}
