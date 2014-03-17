package hu.edudroid.droidlabportal.mobileinterface;

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

public class UploadLogServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8045001540854103637L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    	Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
    	resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");

		PrintWriter out = resp.getWriter();
		out.print(blobs.get("logFile").getKeyString());
		out.flush();
		out.close();
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
