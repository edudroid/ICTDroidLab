package hu.edudroid.ict.logs;

import android.content.ContentValues;
import android.provider.BaseColumns;

public class LogRecord implements BaseColumns {
	public static final String TABLE_NAME = "log_record";
	public static final String COLUMN_NAME_ID = _ID;
	public static final String COLUMN_NAME_MODULE = "module";
	public static final String COLUMN_NAME_LOG_LEVEL = "log_level";
	public static final String COLUMN_NAME_DATE = "date";
	public static final String COLUMN_NAME_MESSAGE = "message";
	
	private long recordId;
	private String module;
	private String logLevel;
	private long date;
	private String message;
	
	public LogRecord(String module, String logLevel, long date, String message) {
		this(-1, module, logLevel, date, message);
	}

	public LogRecord(long id, String module, String logLevel, long date,
			String message) {
		this.recordId = id;
		this.module = module;
		this.logLevel = logLevel;
		this.date = date;
		this.message = message;
	}

	public static String getCreateCommand() {
		return "CREATE TABLE "
			+ TABLE_NAME
			+ " (" + COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
			+ COLUMN_NAME_MODULE + " TEXT,"
			+ COLUMN_NAME_LOG_LEVEL + " TEXT,"
			+ COLUMN_NAME_DATE + " INTEGER,"
			+ COLUMN_NAME_MESSAGE + " TEXT"
			+ " )";
	}

	public static String getDeleteCommand() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	public long getId() {
		return recordId;
	}

	public String getModule() {
		return module;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public long getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_MODULE, module);
		values.put(COLUMN_NAME_LOG_LEVEL, logLevel);
		values.put(COLUMN_NAME_DATE, date);
		values.put(COLUMN_NAME_MESSAGE, message);
		return values;
	}

	public static String[] getAllColumns() {
		return new String[] {
			COLUMN_NAME_ID,
			COLUMN_NAME_MODULE,
			COLUMN_NAME_LOG_LEVEL,
			COLUMN_NAME_DATE,
			COLUMN_NAME_MESSAGE
		};
	}
}
