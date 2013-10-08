package ict.edudroid.appengine;

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

public class GetLogUrlServlet extends HttpServlet{
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String blobUploadUrl = blobstoreService.createUploadUrl("/uploadLog");

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");

		PrintWriter out = resp.getWriter();
		out.print(blobUploadUrl);
		out.flush();
		out.close();
	}
	
}
