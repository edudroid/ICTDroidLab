package hu.edudroid.droidlabportal.mobileinterface;

import hu.edudroid.droidlabportal.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class UploadLogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8045001540854103637L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    	
    	String email = (String)req.getSession().getAttribute(Constants.EMAIL);
    	if (email == null) {
    		resp.sendRedirect("/loginform");
    		return;
    	}
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
    	
    	/*** 
    	 * THIS IS FOR UPLOADING ZIP LOGS
    	 * 
    	Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    	resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");

		PrintWriter out = resp.getWriter();
		out.print(blobs.get("logFile").getKeyString());
		out.flush();
		out.close();
		
		***/
    	try{
    		DatastoreService datastore =
	                DatastoreServiceFactory.getDatastoreService();
    		
	    	int records=Integer.parseInt(req.getParameter(Constants.LOG_COUNT));
	    	
	    	for(int i=0;i<records;i++){
	    	
		    	Entity results = new Entity(Constants.RESULTS_TABLE_NAME,userKey);
		    	results.setProperty(Constants.RESULTS_MODULE_NAME_COLUMN, req.getParameter(i+" "+"module"));
		    	results.setProperty(Constants.RESULTS_LOG_LEVEL_COLUMN, req.getParameter(i+" "+"log_level"));
		    	results.setProperty(Constants.RESULTS_DATE_COLUMN, req.getParameter(i+" "+"date"));
		    	results.setProperty(Constants.RESULTS_MESSAGE_COLUMN, req.getParameter(i+" "+"message"));
		    
		        datastore.put(results);
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
