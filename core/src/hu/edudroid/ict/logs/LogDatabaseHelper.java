package hu.edudroid.ict.logs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDatabaseHelper extends SQLiteOpenHelper {

	private static final String NAME = "log_database.db";
	private static final int VERSION = 1;

	public LogDatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(LogRecord.getCreateCommand());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
