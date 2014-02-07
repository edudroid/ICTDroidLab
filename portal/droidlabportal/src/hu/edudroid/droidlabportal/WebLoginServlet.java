package hu.edudroid.droidlabportal;

import java.io.IOException;
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
public class WebLoginServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String email = req.getParameter(Constants.EMAIL);
		String password = req.getParameter(Constants.PASSWORD);
		// Check for password in datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key userRootKey = KeyFactory.createKey(Constants.USER_TABLE_NAME, Constants.USER_ROOT);
		// Run an ancestor query to ensure we see the most up-to-date
		// view of the Greetings belonging to the selected Guestbook.
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
			}
		} else {
			// Check password
			String storedPassword = (String) users.get(0).getProperty(Constants.USER_PASS_COLUMN);
			if ((password == null) || (!password.equals(storedPassword))) {
				req.setAttribute(Constants.ERROR, "Incorrect password, please try again.");
				RequestDispatcher dispatcher = req.getRequestDispatcher("/loginform");
				try {
					dispatcher.forward(req, resp);
					return;
				} catch (ServletException e) {
					resp.sendError(503);
					return;
				}
			}
			// Log in user
	        req.getSession().setAttribute(Constants.EMAIL, email);
	        resp.sendRedirect("/userhome");
		}
	}
}