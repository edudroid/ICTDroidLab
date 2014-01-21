package hu.edudroid.ict.logs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LogDatabaseManager {
	
	private static final String TAG = LogDatabaseManager.class.getName();
	private SQLiteDatabase db;

	public LogDatabaseManager(Context context) {
		LogDatabaseHelper helper = new LogDatabaseHelper(context);
		db = helper.getWritableDatabase();
	}
	
	public long saveRecord(LogRecord record) {
		long result = db.insert(LogRecord.TABLE_NAME, null, record.getValues());
		return result;
	}
	
	public boolean purgeRecord(long id) {
		int deleted = db.delete(LogRecord.TABLE_NAME, LogRecord._ID + "=?", new String[]{Long.toString(id)});
		if (deleted == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void destroy() {
		try {
			db.close();
		} catch (Exception e) {
			Log.e(TAG, "Couldn't close database", e);
		}
	}

	public List<LogRecord> getRecords(int recordCount) {
		Cursor cursor = db.query(LogRecord.TABLE_NAME, LogRecord.getAllColumns(), "", null, null, null, LogRecord._ID + " DESC", Integer.toString(recordCount));
		List<LogRecord> result = new ArrayList<LogRecord>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			long id = cursor.getLong(cursor.getColumnIndexOrThrow(LogRecord._ID));
			String module = cursor.getString(cursor.getColumnIndexOrThrow(LogRecord.COLUMN_NAME_MODULE));
			String logLevel = cursor.getString(cursor.getColumnIndexOrThrow(LogRecord.COLUMN_NAME_LOG_LEVEL));
			long date = cursor.getLong(cursor.getColumnIndexOrThrow(LogRecord.COLUMN_NAME_DATE));
			String message = cursor.getString(cursor.getColumnIndexOrThrow(LogRecord.COLUMN_NAME_MESSAGE));
			LogRecord record = new LogRecord(id, module, logLevel, date, message);
			result.add(record);
			cursor.moveToNext();
		}
		return result;
	}
}
