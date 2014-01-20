package hu.edudroid.ict.logs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class LogDatabaseManager {
	
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
}
