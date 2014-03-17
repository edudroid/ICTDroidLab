package hu.edudroid.droidlabportal;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	@SuppressWarnings("finally")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String email = req.getParameter(Constants.EMAIL);
		String password = req.getParameter(Constants.PASSWORD);
		// Check for password in datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key userRootKey = KeyFactory.createKey(Constants.USER_TABLE_NAME, Constants.USER_ROOT);
	    Filter emailFilter = new FilterPredicate(Constants.USER_EMAIL_COLUMN, FilterOperator.EQUAL, email);
		Query query = new Query(Constants.USER_TABLE_NAME, userRootKey).setFilter(emailFilter);
		List<Entity> users = datastore.prepare(query).asList(Builder.withLimit(1));
		
		if (users.size() < 1) {
			req.setAttribute(Constants.ERROR, "Unknown user, please check your email address.");
			RequestDispatcher dispatcher = req.getRequestDispatcher("/loginform");
			try {
				dispatcher.forward(req, resp);
				return;
			} catch (ServletException e) {
				resp.sendError(503);
				return;
			} finally {
				return;
			}
		}
		
		// Create MessageDigest instance for MD5
        MessageDigest md;
        String generatedPassword;
        try {
			md = MessageDigest.getInstance("MD5");
			//Add password bytes to digest
	        md.update(password.getBytes());
	        //Get the hash's bytes 
	        byte[] bytes = md.digest();
	        //This bytes[] has bytes in decimal format;
	        //Convert it to hexadecimal format
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< bytes.length ;i++)
	        {
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        //Get complete hashed password in hex format
	        generatedPassword = sb.toString();
		
		
			// Check password
			Entity user = users.get(0);
			String storedPassword = (String) user.getProperty(Constants.USER_PASS_COLUMN);
			if ((password == null) || (!generatedPassword.equals(storedPassword))) {
				req.setAttribute(Constants.ERROR, "Incorrect password, please try again.");
				RequestDispatcher dispatcher = req.getRequestDispatcher("/loginform");
				try {
					dispatcher.forward(req, resp);
					return;
				} catch (ServletException e) {
					resp.sendError(503);
					return;
				} finally {
					return;
				}
			}
			// Log in user
	        req.getSession().setAttribute(Constants.USER_KEY, user.getKey());
	        req.getSession().setAttribute(Constants.EMAIL, email);
			if (req.getParameterMap().containsKey(Constants.WEB)) {
				resp.sendRedirect("/userhome");
			} else {
				resp.setContentType("text/plain");
				resp.getWriter().println("LOGGED_IN");
			}
        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}