package hu.edudroid.droidlabportal.mobileinterface;

import hu.edudroid.droidlabportal.Constants;
import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ZippedUploadLogServlet extends HttpServlet {

	private static final long serialVersionUID = -801058089793304022L;
	
	private static final Logger log = Logger.getLogger(ZippedUploadLogServlet.class.getName());
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

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
		
		// Convert zip to map
		Map <String, String> map = null;
		List<BlobKey> blobKeys = null;
		BlobKey fileBlob = null;
		try {			
			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);			
		    blobKeys = blobs.get("upload-file");
			fileBlob = blobKeys.get(0);		
			BlobstoreInputStream bis = new BlobstoreInputStream(fileBlob);
			
			ZipInputStream zin = new ZipInputStream(bis);		
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			while (zin.getNextEntry()!=null) {
				for (int c = zin.read(); c != -1; c = zin.read()) {
					ops.write(c);
				}
				zin.closeEntry();
				ops.close();
			}
			zin.close();
			String data = ops.toString("UTF-8");
			BufferedReader reader = new BufferedReader(new StringReader(data));
			map = new HashMap <String, String>();
			String line = null;
			while ((line = reader.readLine())!=null) {
				String[] split = line.split("\\t");
				map.put(split[0], split[1]);
			}	
			
		} catch (Exception e) {
			resp.setContentType("text/plain");
			resp.getWriter().println("ERROR WHILE CONVERTING ZIP TO MAP "+e.toString());
			return;
		} finally {
			if (blobKeys!=null) {
				blobstoreService.delete(fileBlob);
			}
			if (map==null) {
				return;
			}			
		}
		
		
		
		// Finds device	
		String imei = map.get(Constants.IMEI);
		Key deviceKey = null; // Always search for device key based on IMEI 
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
    	
    	try{

	    	int records = Integer.parseInt(map.get(Constants.LOG_COUNT));
	    	
	    	// Logs are added on device level
	    	for(int i=0; i<records; i++){
		    	Entity record = new Entity(Constants.RESULTS_TABLE_NAME, deviceKey);
		    	record.setProperty(Constants.RESULTS_MODULE_NAME_COLUMN, map.get(i+" module"));
		    	record.setProperty(Constants.RESULTS_LOG_LEVEL_COLUMN, map.get(i+" log_level"));
		    	record.setProperty(Constants.RESULTS_DATE_COLUMN, map.get(i+" date"));
		    	record.setProperty(Constants.RESULTS_MESSAGE_COLUMN, map.get(i+" message"));
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

    }

}
