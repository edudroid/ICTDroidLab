package hu.edudroid.droidlabportal.mobileinterface;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class GetLogUrlServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8602087197512812848L;
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
