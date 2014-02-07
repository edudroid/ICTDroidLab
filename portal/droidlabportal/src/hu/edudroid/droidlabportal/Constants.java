package hu.edudroid.droidlabportal;

public class Constants {
	public static final String INDEX = "index";
	public static final String FEATURES = "features";
	public static final String TEAM = "team";
	public static final String NEWS = "news";
	public static final String REG_FORM = "regform";
	public static final String PROFILE = "profile";
	public static final String DEVICE = "device";
	public static final String DEVICES = "devices";
	public static final String LOGIN = "login";
	
	public static final String EMAIL = "email";
	public static final String PASSWORD = "pass";
	public static final String PASSWORDCHECK = "passagain";

	public static final String ERROR = "ERROR";

	public static final String USER_ROOT = "user_root";
	public static final String USER_TABLE_NAME = "users";
	public static final String USER_EMAIL_COLUMN = "email";
	public static final String USER_PASS_COLUMN = "password";
	public static final String USER_REGISTRATION_DATE_COLUMN = "reg_date";
	
	public static boolean isValidPassword(String password) {
		if (password==null) {
			return false;
		}
		if (password.length() < 6) {
			return false;
		}
		return true;
	}
}
