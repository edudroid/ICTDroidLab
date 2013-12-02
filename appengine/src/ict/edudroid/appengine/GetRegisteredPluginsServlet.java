package ict.edudroid.appengine;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class GetRegisteredPluginsServlet extends HttpServlet{
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		
	    Query query = new Query("RegisteredPlugin");
	    query.addSort("date",SortDirection.DESCENDING);
	    List<Entity> plugins = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
	    
	    try {
	    	JSONArray pluginRespArray = new JSONArray();
	    	for(Entity plugin : plugins){
	    		JSONObject pluginObject = new JSONObject();
		        pluginObject.put("name", plugin.getProperty("name"));
		        pluginObject.put("class", plugin.getProperty("class"));
		        pluginObject.put("version", plugin.getProperty("version"));
		        pluginRespArray.put(pluginObject);
	    	}
	        pluginRespArray.write(resp.getWriter());
	    } catch (JSONException e) {
	        System.err
	        .println("Failed to create JSON response: " + e.getMessage());
	    }
	}
	
}
