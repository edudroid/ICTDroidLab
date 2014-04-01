package hu.edudroid.droidlabportal;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SignOutServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().removeAttribute(Constants.EMAIL);
        req.getSession().removeAttribute(Constants.USER_KEY);
        req.getSession().removeAttribute(Constants.ROLE);
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie:cookies) {
				if (cookie.getName().equals(Constants.DROID_LAB_LOGIN_COOKIE)) {
					cookie.setMaxAge(0);
					resp.addCookie(cookie);
				}
			}
		}

		if (req.getParameterMap().containsKey(Constants.WEB)) {
	        resp.sendRedirect("/");
		} else {
			resp.setContentType("text/plain");
			resp.getWriter().println("LOGGED_OUT");
		}
	}
}