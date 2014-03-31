package hu.edudroid.droidlabportal.user;

import hu.edudroid.droidlabportal.Constants;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class UserManager {
	private static final Logger log = Logger.getLogger(UserManager.class.getName());

	/**
	 * Gets logged in user, and adds user's login cookie to the response
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	public static User checkUser(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String email = (String)session.getAttribute(Constants.EMAIL);		
		Key userKey = (Key)request.getSession().getAttribute(Constants.USER_KEY);
		String role = (String)session.getAttribute(Constants.ROLE);
		
		// If user's session is not logged in yet, check for DroidLabLogin cookie
		if (email != null) {
			log.info("Email available in session " + email);
			User user = new User(null, null, email, userKey, role);
			return user;
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			log.info("Request has " + cookies.length + " cookies.");
			for (Cookie cookie:cookies) {
				log.info("Checking cookie " + cookie.getName());
				if (cookie.getName().equals(Constants.DROID_LAB_LOGIN_COOKIE)) {
					// Get user from datastore for cookie
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				    Key userRootKey = KeyFactory.createKey(Constants.USER_TABLE_NAME, Constants.USER_ROOT);
				    Query.Filter emailFilter = new FilterPredicate(Constants.USER_LOGIN_COOKIE_COLUMN, FilterOperator.EQUAL, cookie.getValue());
					Query query = new Query(Constants.USER_TABLE_NAME, userRootKey).setFilter(emailFilter);
					List<Entity> users = datastore.prepare(query).asList(Builder.withLimit(1));
					if (users.size() == 1) {
						Entity user = users.get(0);
						long expirationDate = Constants.COOKIE_EXPIRATION * 1000l + (Long)user.getProperty(Constants.USER_LAST_LOGIN);
						if (expirationDate > System.currentTimeMillis()) {
							email = (String)user.getProperty(Constants.USER_EMAIL_COLUMN);
							userKey = user.getKey();
							role=(String)user.getProperty(Constants.USER_ROLE_COLUMN);
							// Add user params to session
							session.setAttribute(Constants.EMAIL, email);
							session.setAttribute(Constants.USER_KEY, userKey);
							session.setAttribute(Constants.ROLE, role);
							User ret = new User(null, null, email, userKey, role);
							log.info("Valid login cookie found for user " + email);
							return ret;
						} else {
							log.warning("Cookie expired");
							// Remove the cookie from the session
							cookie.setMaxAge(0);
							session.removeAttribute(Constants.EMAIL);
							session.removeAttribute(Constants.USER_KEY);
							session.removeAttribute(Constants.ROLE);
							response.addCookie(cookie);
							System.out.println("Cookie timed out and removed.");
							// Remove the cookie from the datastore
							user.removeProperty(Constants.USER_LOGIN_COOKIE_COLUMN);
							datastore.put(user);
							return null;
						}
					} else {
						log.severe("No user found for cookie.");
						return null;
					}
				}
			}
			log.warning("Login cookie not present.");
			return null;
		} else {
			log.warning("No cookies or email present.");
			return null;
		}
	}
}
