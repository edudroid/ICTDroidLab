package ict.edudroid.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class UploadLogServlet extends HttpServlet {
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
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
    	
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("logFile");

        /*
        if (blobKey == null) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/serveLog?blob-key=" + blobKey.getKeyString());
        }
        */
    }
}
