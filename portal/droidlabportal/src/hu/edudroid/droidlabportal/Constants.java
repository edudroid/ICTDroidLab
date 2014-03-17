package hu.edudroid.droidlabportal;

public class Constants {

	// Page names
	public static final String INDEX = "index";
	public static final String FEATURES = "features";
	public static final String TEAM = "team";
	public static final String NEWS = "news";
	public static final String REG_FORM = "regform";
	public static final String PROFILE = "profile";
	public static final String MODULES = "modules";
	public static final String DEVICE = "device";
	public static final String DEVICES = "devices";
	public static final String UPLOADMODULE = "uploadmodule";
	public static final String RESULTS = "results";
	public static final String MEASUREMENT = "measurement";
	public static final String LOGIN = "login";
	
	// Form parameters 
	public static final String WEB = "web";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "pass";
	public static final String PASSWORDCHECK = "passagain";
	public static final String IMEI = "IMEI";

	// Session parameters
	public static final String USER_KEY = "user key";

	// Datastore names
	public static final String USER_ROOT = "user_root";
	public static final String USER_TABLE_NAME = "users";
	public static final String USER_EMAIL_COLUMN = "email";
	public static final String USER_PASS_COLUMN = "password";
	public static final String USER_REGISTRATION_DATE_COLUMN = "reg_date";
	
	public static final String MYDEVICE_TABLE_NAME = "mydevices";
	public static final String MYDEVICE_IMEI_COLUMN = "imei";
	
	public static final String MODULES_TABLE_NAME = "modules";
	public static final String MODULES_AUTHOR_COLUMN = "author";
	public static final String MODULES_CLASS_COLUMN = "class_name";
	public static final String MODULES_DATE_COLUMN = "date";
	public static final String MODULES_DESC_FILE_KEY_COLUMN = "descFileBlobKey";
	public static final String MODULES_JAR_FILE_KEY_COLUMN = "jarFileBlobKey";
	public static final String MODULES_JAR_FILE_COLUMN = "jar_file";
	public static final String MODULES_DESC_FILE_COLUMN = "desc_file";
	public static final String MODULES_EMAIL_COLUMN = "email";
	public static final String MODULES_NAME_COLUMN = "module_name";
	
	public static final String DEVICES_TABLE_NAME = "RegisteredDevices";
	public static final String DEVICES_IMEI_COLUMN = "imei";
	public static final String DEVICES_SDK_COLUMN = "sdk_version";
	public static final String DEVICES_CELLULAR_COLUMN = "cellular";
	public static final String DEVICES_WIFI_COLUMN = "wifi";
	public static final String DEVICES_GPS_COLUMN = "gps";
	public static final String DEVICES_BLUETOOTH_COLUMN = "bluetooth";
	public static final String DEVICES_DATE_COLUMN = "date";
		
	
	// Error message
	public static final String ERROR = "ERROR";
	public static final String ERROR_NOT_LOGGED_IN = ERROR + "[1]: Not logged in.";
	public static final String ERROR_DEVICE_ALREADY_EXISTS = ERROR + "[2]: Device already registered.";
	public static final String ERROR_MISSING_IMEI = ERROR + "[3]: Missing IMEI.";
	
	// Other stuff
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
