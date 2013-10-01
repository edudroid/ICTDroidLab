package ict.edudroid.appengine;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class RefreshMetaDatasServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		Entity metadata = new Entity("MetaDatas",req.getParameter("imei"));
		metadata.setProperty("sdk_version", req.getParameter("sdk_version"));
		metadata.setProperty("mobile", req.getParameter("mobile"));
		metadata.setProperty("wifi", req.getParameter("wifi"));
		metadata.setProperty("bluetooth", req.getParameter("bluetooth"));
		metadata.setProperty("gps", req.getParameter("gps"));
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(metadata);
	}
}
