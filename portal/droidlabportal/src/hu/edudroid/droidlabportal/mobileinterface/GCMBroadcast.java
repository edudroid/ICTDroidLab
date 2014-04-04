package hu.edudroid.droidlabportal.mobileinterface;

import hu.edudroid.droidlabportal.Constants;
import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class GCMBroadcast extends HttpServlet {
    private static final String myApiKey = "AIzaSyCx3nQWdbCvQK5UjCmOi9OfDCiiWZOo3l4";
    private List<String> androidTargets = new ArrayList<String>();
    private static final long serialVersionUID = 1L;

    /*protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

            doPost( req, resp);*/


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int device_count = Integer.parseInt(req.getParameter("device_count"));
        
        User user = UserManager.checkUser(req.getSession(), req, resp);
        if (user == null) {
        	resp.sendRedirect("/loginform");
        	return;
        }
        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	
    	Query query = new Query(Constants.DEVICE_TABLE_NAME);
    	query.addSort(Constants.DEVICE_MODULES_COUNT_COLUMN, SortDirection.ASCENDING);
    	List<Entity> devices = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(device_count));
    	
    	Query.Filter moduleFilter = new Query.FilterPredicate(Constants.MODULES_MODULE_ID_COLUMN, Query.FilterOperator.EQUAL, req.getParameter("module"));
    	query = new Query(Constants.MODULES_TABLE_NAME).setFilter(moduleFilter);
    	List<Entity> module = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    	
    	if(module.size()!=1){
    		resp.setContentType("text/plain");
			resp.getWriter().println("NO MODULES FOUND");
			return;
    	}
    	
    	String userMessage = "http://droidlabportal.appspot.com/serveJar?blob-key="+module.get(0).getProperty(Constants.MODULES_JAR_FILE_KEY_COLUMN)
    			+" http://droidlabportal.appspot.com/serveDescriptor?blob-key="+module.get(0).getProperty(Constants.MODULES_DESC_FILE_KEY_COLUMN);
    	
        for(Entity device : devices){
        	int modules_count=Integer.parseInt(device.getProperty(Constants.DEVICE_MODULES_COUNT_COLUMN).toString());
        	List<String> modules_id;
        	List<String> modules_exp;
        	if (modules_count==0){
        		modules_id=new ArrayList<String>();
        		modules_exp=new ArrayList<String>();
        	}
        	else{
        		modules_id=(List<String>) device.getProperty(Constants.DEVICE_MODULES_ID_COLUMN);
	        	modules_exp=(List<String>) device.getProperty(Constants.DEVICE_MODULES_EXPIRATION_COLUMN);
        	}
	        	
        	modules_id.add(req.getParameter("module"));
        	// TODO
        	// NOW + MEASUREMENTH LENGTH
        	long exp_time=System.currentTimeMillis()+Long.parseLong(module.get(0).getProperty(Constants.MODULES_MEASUREMENT_LENGTH_COLUMN).toString())*60*60*60;
        	modules_exp.add(String.valueOf(exp_time));
        	// ****
        	
        	Entity updatedDevice=new Entity(Constants.DEVICE_TABLE_NAME,device.getKey());
        	updatedDevice.setProperty(Constants.DEVICE_IMEI_COLUMN, device.getProperty(Constants.DEVICE_IMEI_COLUMN));
        	updatedDevice.setProperty(Constants.DEVICE_NAME_COLUMN, device.getProperty(Constants.DEVICE_NAME_COLUMN));
        	updatedDevice.setProperty(Constants.DEVICE_GCM_ID_COLUMN, device.getProperty(Constants.DEVICE_GCM_ID_COLUMN));
        	updatedDevice.setProperty(Constants.DEVICE_SDK_VERSION_COLUMN, device.getProperty(Constants.DEVICE_SDK_VERSION_COLUMN));
        	updatedDevice.setProperty(Constants.DEVICE_DATE_COLUMN, new Date());
        	updatedDevice.setProperty(Constants.DEVICE_MODULES_COUNT_COLUMN, modules_count+1);
        	updatedDevice.setProperty(Constants.DEVICE_MODULES_ID_COLUMN, modules_id);
        	updatedDevice.setProperty(Constants.DEVICE_MODULES_EXPIRATION_COLUMN, modules_exp);
        	
        	datastore.delete(device.getKey());
        	device=updatedDevice;
        	datastore.put(device);
        	
        	androidTargets.add((String)device.getProperty(Constants.DEVICE_GCM_ID_COLUMN));
        }
        
        if(androidTargets.size()==0){
        	resp.setContentType("text/plain");
			resp.getWriter().println("NO DEVICES AVAIBLE");
			return;
        }
        Sender sender = new Sender(myApiKey);
        Message message = new Message.Builder().addData("message", userMessage)
                .build();

        MulticastResult result = sender.send(message, androidTargets, 1);

        if (result.getResults() != null) {
        	log(result.getResults().toString());
            int canonicalRegId = result.getCanonicalIds();
            
            resp.sendRedirect("/measurement?succes=true");
        } else {
            int error = result.getFailure();
            resp.getWriter().printf("ERROR: ["+error+"]! GCM broadcast not sent...");
        }
        
    }

}