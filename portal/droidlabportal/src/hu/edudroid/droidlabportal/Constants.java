package hu.edudroid.droidlabportal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Constants {

	// Page names
	public static final String INDEX = "index";
	public static final String FEATURES = "features";
	public static final String TEAM = "team";
	public static final String NEWS = "news";
	public static final String REG_FORM = "regform";
	public static final String PROFILE = "profile";
	public static final String MODULE = "module";
	public static final String MODULES = "modules";
	public static final String DEVICE = "device";
	public static final String DEVICES = "devices";
	public static final String UPLOADMODULE = "uploadmodule";
	public static final String RESULTS = "results";
	public static final String MEASUREMENT = "measurement";
	public static final String LOGIN = "login";
	public static final String USERS = "users";
	
	// Form parameters 
	public static final String WEB = "web";
	public static final String EMAIL = "email";
	public static final String ROLE = "role";
	public static final String PASSWORD = "pass";
	public static final String PASSWORDCHECK = "passagain";
	public static final String IMEI = "IMEI";
	public static final String DEVICE_NAME = "device_name";
	public static final String GCM_ID = "gcm_id";
	public static final String SDK_VERSION = "sdk_version";
	
	//Role parameters
	public static final String ROLE_USER = "user";
	public static final String ROLE_RESEARCHER = "researcher";
	public static final String ROLE_ADMIN = "admin";

	// Session parameters
	public static final String USER_KEY = "user key";
	public static final String DEVICE_KEY = "device key";
	public static final String DEVICE_IMEI_KEY = "device key";

	// Datastore names
	public static final String USER_ROOT = "user_root";
	public static final String USER_TABLE_NAME = "users";
	public static final String USER_EMAIL_COLUMN = "email";
	public static final String USER_PASS_COLUMN = "password";
	public static final String USER_ROLE_COLUMN = "role";
	public static final String USER_REGISTRATION_DATE_COLUMN = "reg_date";
	public static final String USER_LOGIN_COOKIE_COLUMN = "login_cookie";
	public static final String USER_LAST_LOGIN = "last_login";
	
	public static final String MODULES_TABLE_NAME = "modules";
	public static final String MODULES_MODULE_ID_COLUMN = "module_id";
	public static final String MODULES_MODULE_NAME_COLUMN = "module_name";
	public static final String MODULES_AUTHOR_COLUMN = "author";
	public static final String MODULES_DESCRIPTION_COLUMN = "description";
	public static final String MODULES_WEBSITE_COLUMN = "website";
	public static final String MODULES_MEASUREMENT_LENGTH_COLUMN = "measurement_length";
	public static final String MODULES_USED_PLUGINS_COLUMN = "used_plugins";
	public static final String MODULES_PERMISSIONS_COLUMN = "permissions";
	public static final String MODULES_QUOTAS_COLUMN = "quotas";
	public static final String MODULES_DESC_FILE_KEY_COLUMN = "descFileBlobKey";
	public static final String MODULES_JAR_FILE_KEY_COLUMN = "jarFileBlobKey";
	public static final String MODULES_JAR_FILE_COLUMN = "jar_file";
	public static final String MODULES_CLASS_NAME_COLUMN = "class_name";
	public static final String MODULES_EMAIL_COLUMN = "email";
	public static final String MODULES_DATE_COLUMN = "date";
	
	public static final String DEVICE_TABLE_NAME = "devices";
	public static final String DEVICE_IMEI_COLUMN = "IMEI";
	public static final String DEVICE_NAME_COLUMN = "device_name";
	public static final String DEVICE_GCM_ID_COLUMN = "gcm_id";
	public static final String DEVICE_SDK_VERSION_COLUMN = "sdk_version";		
	public static final String DEVICE_DATE_COLUMN = "date";
	public static final String DEVICE_CELLULAR_COLUMN = "cellular";
	public static final String DEVICE_WIFI_COLUMN = "wifi";
	public static final String DEVICE_BLUETOOTH_COLUMN = "bluetooth";
	public static final String DEVICE_GPS_COLUMN = "gps";
	
	public static final String RESULTS_TABLE_NAME = "results";
	public static final String RESULTS_MODULE_NAME_COLUMN = "module_name";
	public static final String RESULTS_LOG_LEVEL_COLUMN = "log_level";
	public static final String RESULTS_DATE_COLUMN = "date";		
	public static final String RESULTS_MESSAGE_COLUMN = "message";	
	
	// Error message
	public static final String ERROR = "ERROR";
	public static final String ERROR_NOT_LOGGED_IN = ERROR + "[1]: Not logged in.";
	public static final String ERROR_DEVICE_ALREADY_EXISTS = ERROR + "[2]: Device already registered.";
	public static final String ERROR_MISSING_IMEI = ERROR + "[3]: Missing IMEI.";
	public static final String ERROR_MISSING_DEVICE_NAME = ERROR + "[3]: Missing device name.";
	public static final String ERROR_MISSING_GCM_ID = ERROR + "[3]: Missing gcm id.";
	public static final String ERROR_MISSING_SDK_VERSION = ERROR + "[3]: Missing sdk version.";
	public static final String ERROR_NO_DEVICE_KEY = ERROR + "[3]: No device key in session.";
	
	// Other stuff
	public static final String DROID_LAB_LOGIN_COOKIE = "DROID_LAB_LOGIN_COOKIE";
	public static final int COOKIE_EXPIRATION = 14 * 24 * 3600;
	public static final String DOMAIN = "localhost";
	public static final String LOG_COUNT = "log_count";
	
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY.MM.dd", Locale.getDefault());
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("MM.dd HH:mm:ss.SSS", Locale.getDefault());
	
	public static boolean isValidPassword(String password) {
		if (password==null) {
			return false;
		}
		if (password.length() < 6) {
			return false;
		}
		return true;
	}
	
	public static String formatDate(Date date){
		return dateFormatter.format(date);
	}

	public static String formatTime(Date date){
		return timeFormatter.format(date);
	}
}