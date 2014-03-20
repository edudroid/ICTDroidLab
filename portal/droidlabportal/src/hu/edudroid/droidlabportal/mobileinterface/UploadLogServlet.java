package hu.edudroid.droidlabportal.mobileinterface;

import hu.edudroid.droidlabportal.Constants;
import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
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

public class UploadLogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8045001540854103637L;
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    	
    	// Find user
    	User user = UserManager.checkUser(req.getSession(), req, resp);
    	
		if (user == null) {
			if (req.getParameterMap().containsKey(Constants.WEB)) {
		        resp.sendRedirect("/loginform");
			} else {
				resp.setContentType("text/plain");
				resp.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			}
			return;
		}

		// Finds device
		String imei = req.getParameter(Constants.IMEI);
		Key deviceKey = null;
		if (imei == null) {
			// Checks device registered to the active session
			deviceKey = (Key)req.getSession().getAttribute(Constants.DEVICE_KEY);
		}
		
		if (deviceKey == null) {
			DatastoreService datastore =
	                DatastoreServiceFactory.getDatastoreService();
			
			Filter deviceFilter = new FilterPredicate(Constants.DEVICE_IMEI_COLUMN, FilterOperator.EQUAL, imei);
			Query query = new Query(Constants.DEVICE_TABLE_NAME, user.getKey()).setFilter(deviceFilter); // Use ancestor query to only query devices registered to the user
			List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
			if(devices.size()>0){
				// Found the device to use
				deviceKey=devices.get(0).getKey();
				req.getSession().setAttribute(Constants.DEVICE_KEY, deviceKey);
				req.getSession().setAttribute(Constants.DEVICE_IMEI_KEY, imei);
			} else {
				resp.setContentType("text/plain");
				resp.getWriter().println(Constants.ERROR_NO_DEVICE_KEY);
				return;
			}
		}
    	
    	try{
    		DatastoreService datastore =
	                DatastoreServiceFactory.getDatastoreService();
    		
	    	int records = Integer.parseInt(req.getParameter(Constants.LOG_COUNT));
	    	
	    	for(int i=0;i<records;i++){
	    	
		    	Entity record = new Entity(Constants.RESULTS_TABLE_NAME,deviceKey);
		    	record.setProperty(Constants.RESULTS_MODULE_NAME_COLUMN, req.getParameter(i+" "+"module"));
		    	record.setProperty(Constants.RESULTS_LOG_LEVEL_COLUMN, req.getParameter(i+" "+"log_level"));
		    	record.setProperty(Constants.RESULTS_DATE_COLUMN, req.getParameter(i+" "+"date"));
		    	record.setProperty(Constants.RESULTS_MESSAGE_COLUMN, req.getParameter(i+" "+"message"));
		        datastore.put(record);
	    	}
	    	resp.getWriter().printf(records + " logs were uploaded succesfully");
    	} catch (Exception e){
    		e.printStackTrace();
    		resp.getWriter().printf("Failed to upload logs!");
    	}
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws ServletException, IOException {
    	
        Entity logs = new Entity("Logs");
        logs.setProperty("imei", req.getParameter("imei"));
        logs.setProperty("logFileBlobKey", req.getParameter("blobkey"));
        logs.setProperty("date", new Date());
        
        String info=req.getParameter("imei")+" "+
        		req.getParameter("blobkey");
        
        log(info);
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(logs);
    }
}
