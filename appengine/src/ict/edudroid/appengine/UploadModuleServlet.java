package ict.edudroid.appengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;

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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UploadModuleServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
    	UserService userService = UserServiceFactory.getUserService();
    	
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
			Entity module = new Entity("Modules");
			module.setProperty("email", userService.getCurrentUser().getEmail());
			module.setProperty("nickname", userService.getCurrentUser().getNickname());
			module.setProperty("desc_file", json.get("desc_file"));
			module.setProperty("jar_file", json.get("jar_file"));
			module.setProperty("module_name", json.get("module_name"));
			module.setProperty("class_name", json.get("class_name"));
			module.setProperty("jarFileBlobKey", blobs.get("jarFile").getKeyString());
			module.setProperty("descFileBlobKey", blobs.get("descFile").getKeyString());
			module.setProperty("date", new Date());
			
			String info=userService.getCurrentUser().getEmail()+" "+
					json.get("desc_file")+" "+
					json.get("jar_file")+" "+
					json.get("module_name")+" "+
					json.get("class_name");
			log(info);
			
			DatastoreService datastore =
	                DatastoreServiceFactory.getDatastoreService();
	        datastore.put(module);     
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
        
        
     
        
        /*
        if (blobKey == null) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/parseModuleDesc?blob-key=" + blobKey.getKeyString());
        }*/
    }
}
