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
public class RegisterNewPluginServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Entity registerplugin = new Entity("RegisteredPlugin");
		registerplugin.setProperty("name", req.getParameter("plugin_name"));
		registerplugin.setProperty("class", req.getParameter("plugin_class"));
		registerplugin.setProperty("version", req.getParameter("plugin_version"));
		registerplugin.setProperty("desc", req.getParameter("plugin_desc"));
		registerplugin.setProperty("date", new Date());
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(registerplugin);
	}
}
