package hu.edudroid.droidlabportal.mobileinterface;

import hu.edudroid.droidlabportal.Constants;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class ServeDescriptorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7263518365946303369L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException {
    	DatastoreService datastore=DatastoreServiceFactory.getDatastoreService();
		
    	
    	Filter moduleFilter = new FilterPredicate(Constants.MODULES_DESC_FILE_KEY_COLUMN, FilterOperator.EQUAL, req.getParameter("blob-key"));
		Query query = new Query(Constants.MODULES_TABLE_NAME).setFilter(moduleFilter);
	    List<Entity> modules = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
    	
	    if(modules.size()==1){
	    	BlobKey blobKey = new BlobKey(req.getParameter("blob-key")); //example
		    String fileName = modules.get(0).getProperty(Constants.MODULES_JAR_FILE_COLUMN).toString();
		    res.setHeader("Content-Disposition", "attachment; filename=\"" +fileName +".desc\"");
		    		
		    blobstoreService.serve(blobKey, res);
	    }
    }
}
