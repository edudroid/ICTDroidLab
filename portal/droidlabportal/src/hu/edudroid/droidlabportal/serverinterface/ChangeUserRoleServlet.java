package hu.edudroid.droidlabportal.serverinterface;

import hu.edudroid.droidlabportal.Constants;
import hu.edudroid.droidlabportal.user.User;
import hu.edudroid.droidlabportal.user.UserManager;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class ChangeUserRoleServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query.Filter emailFilter = new FilterPredicate(Constants.USER_EMAIL_COLUMN, FilterOperator.EQUAL, req.getParameter(Constants.EMAIL));
		Query query = new Query(Constants.USER_TABLE_NAME).setFilter(emailFilter);
		List<Entity> users = datastore.prepare(query).asList(Builder.withLimit(1));
		
		Entity updatedUser=new Entity(users.get(0).getKey());
		updatedUser.setProperty(Constants.USER_EMAIL_COLUMN, users.get(0).getProperty(Constants.USER_EMAIL_COLUMN));
		updatedUser.setProperty(Constants.USER_LAST_LOGIN, users.get(0).getProperty(Constants.USER_LAST_LOGIN));
		updatedUser.setProperty(Constants.USER_LOGIN_COOKIE_COLUMN, users.get(0).getProperty(Constants.USER_LOGIN_COOKIE_COLUMN));
		updatedUser.setProperty(Constants.USER_PASS_COLUMN, users.get(0).getProperty(Constants.USER_PASS_COLUMN));
		updatedUser.setProperty(Constants.USER_REGISTRATION_DATE_COLUMN, users.get(0).getProperty(Constants.USER_REGISTRATION_DATE_COLUMN));
		updatedUser.setProperty(Constants.USER_ROLE_COLUMN, req.getParameter(Constants.ROLE));
		datastore.put(updatedUser);
		
		resp.sendRedirect("/users");
	}
}
