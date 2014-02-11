package hu.edudroid.droidlabportal;

import java.io.IOException;
import java.util.Date;
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
public class RegisterUserServlet extends HttpServlet {

	@SuppressWarnings("finally")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String email = req.getParameter(Constants.EMAIL);
		String password = req.getParameter(Constants.PASSWORD);
		String passcheck = req.getParameter(Constants.PASSWORDCHECK);
		if (password == null || !password.equals(passcheck)) {
			req.setAttribute(Constants.ERROR, "Password and check doesn't match.");
			RequestDispatcher dispatcher = req.getRequestDispatcher("/regform");
			try {
				dispatcher.forward(req, resp);
				return;
			} catch (ServletException e) {
				resp.sendError(503);
				return;
			}
		} else if(!Constants.isValidPassword(password)){
			req.setAttribute(Constants.ERROR, "Invalid password.");
			RequestDispatcher dispatcher = req.getRequestDispatcher("/regform");
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
		// Check for password in datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key userRootKey = KeyFactory.createKey(Constants.USER_TABLE_NAME, Constants.USER_ROOT);
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
	    Filter emailFilter = new FilterPredicate(Constants.USER_EMAIL_COLUMN, FilterOperator.EQUAL, email);
		Query query = new Query(Constants.USER_TABLE_NAME, userRootKey).setFilter(emailFilter);
		List<Entity> users = datastore.prepare(query).asList(Builder.withLimit(1));
		if (users.size() > 0) {
			req.setAttribute(Constants.ERROR, "Email address is already in use.");
			RequestDispatcher dispatcher = req.getRequestDispatcher("/regform");
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
		// Add user
        Entity user = new Entity(Constants.USER_TABLE_NAME, userRootKey);
        user.setProperty(Constants.USER_EMAIL_COLUMN, email);
        user.setProperty(Constants.USER_PASS_COLUMN, password);
        user.setProperty(Constants.USER_REGISTRATION_DATE_COLUMN, new Date());	        
        Key userKey = datastore.put(user);
        req.getSession().setAttribute(Constants.USER_KEY, userKey);
        req.getSession().setAttribute(Constants.EMAIL, email);
		if (req.getParameterMap().containsKey(Constants.WEB)) {
			resp.sendRedirect("/userhome");
		} else {
			resp.setContentType("text/plain");
			resp.getWriter().println("REGISTERED");
		}
	}
}