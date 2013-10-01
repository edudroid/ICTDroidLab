package ict.edudroid.appengine;

import java.io.IOException;
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

public class UploadModuleServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);

        Entity module = new Entity("Modules");
		module.setProperty("user", req.getParameter("user"));
		module.setProperty("jarFileBlobKey", blobs.get("jarFile").getKeyString());
		module.setProperty("descFileBlobKey", blobs.get("descFile").getKeyString());
		module.setProperty("date", new Date());
        
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        datastore.put(module);
        
        /*
        if (blobKey == null) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/serveModule?blob-key=" + blobKey.getKeyString());
        }
        */
    }
}
