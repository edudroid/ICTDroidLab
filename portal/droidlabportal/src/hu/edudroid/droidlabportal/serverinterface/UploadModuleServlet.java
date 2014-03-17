package hu.edudroid.droidlabportal.serverinterface;

import hu.edudroid.droidlabportal.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UploadModuleServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	String email = (String)req.getSession().getAttribute(Constants.EMAIL);
    	if (email == null) {
    		res.sendRedirect("/loginform");
    		return;
    	}
    	Key userKey = null;
		try {
			userKey = (Key)req.getSession().getAttribute(Constants.USER_KEY);
		} catch (Exception e) {
			res.setContentType("text/plain");
			res.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		}
		if (userKey == null) {
			res.setContentType("text/plain");
			res.getWriter().println(Constants.ERROR_NOT_LOGGED_IN);
			return;
		}
    	
    	Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    	
		InputStream is = new BlobstoreInputStream(blobs.get("descFile"));
		InputStreamReader ir = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(ir);
		StringBuilder responseStrBuilder = new StringBuilder();

	    String inputStr;
	    while ((inputStr = br.readLine()) != null)
	        responseStrBuilder.append(inputStr);
		br.close();
		
		try {
			JSONObject json=new JSONObject(responseStrBuilder.toString());
			Entity module = new Entity(Constants.MODULES_TABLE_NAME);

			module.setProperty(Constants.MODULES_EMAIL_COLUMN,email);
			module.setProperty(Constants.MODULES_AUTHOR_COLUMN, json.get("author"));
			module.setProperty(Constants.MODULES_DESC_FILE_COLUMN, json.get("desc_file"));
			module.setProperty(Constants.MODULES_JAR_FILE_COLUMN, json.get("jar_file"));
			module.setProperty(Constants.MODULES_NAME_COLUMN, json.get("module_name"));
			module.setProperty(Constants.MODULES_CLASS_COLUMN, json.get("class_name"));
			module.setProperty(Constants.MODULES_JAR_FILE_KEY_COLUMN, blobs.get("jarFile").getKeyString());
			module.setProperty(Constants.MODULES_DESC_FILE_KEY_COLUMN, blobs.get("descFile").getKeyString());
			module.setProperty(Constants.MODULES_DATE_COLUMN, new Date());
			
			DatastoreService datastore =
	                DatastoreServiceFactory.getDatastoreService();
	        datastore.put(module);
	        
	        res.sendRedirect("/uploadmodule?succes=true");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log(e.getMessage());
			res.sendRedirect("/uploadmodule?succes=false");
		}
    }
}
