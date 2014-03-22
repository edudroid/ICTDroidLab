package hu.edudroid.droidlabportal.mobileinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class GetRegisteredPluginsServlet extends HttpServlet{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 134445733702007333L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		
	    Query query = new Query("RegisteredPlugin");
	    query.addSort("date",SortDirection.DESCENDING);
	    List<Entity> plugins = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	    
    	PrintWriter writer = resp.getWriter();
        writer.println("[");
    	for(Entity plugin : plugins){
    		writer.print("{");
    		writer.print("\"name\":");
    		writer.print("\"" + plugin.getProperty("name") + "\",");
    		writer.print("\"class\":");
    		writer.print("\"" + plugin.getProperty("class") + "\",");
    		writer.print("\"version\":");
    		writer.print(plugin.getProperty("version") + ",");
    		writer.print("\"description\":");
    		writer.print("\"" + plugin.getProperty("desc") + "\"");
    		writer.println("},");
    	}
        writer.print("]");
	}
	
}
