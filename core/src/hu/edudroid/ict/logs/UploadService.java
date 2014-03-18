package hu.edudroid.ict.logs;

import hu.edudroid.ict.utils.HttpUtils;
import hu.edudroid.ict.utils.ServerUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UploadService extends IntentService {

	public static final String NAME = UploadService.class.getName();

	private static final String TAG = UploadService.class.getName();

	private static final int UPLOAD_BATCH_SIZE = 20;

	private static final String LOG_COUNT = "log_count";
	
	private LogDatabaseManager databaseManager;
	
	public UploadService() {
		super(NAME);
		databaseManager = new LogDatabaseManager(this); 
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			LogRecord logRecord = new LogRecord(
					intent.getStringExtra(LogRecord.COLUMN_NAME_MODULE),
					intent.getStringExtra(LogRecord.COLUMN_NAME_LOG_LEVEL),
					intent.getLongExtra(LogRecord.COLUMN_NAME_DATE, 0),
					intent.getStringExtra(LogRecord.COLUMN_NAME_MESSAGE));
			List<LogRecord> recordsToUpload = databaseManager.getRecords(UPLOAD_BATCH_SIZE - 1);
			recordsToUpload.add(logRecord);
			boolean result = uploadLogs(recordsToUpload, this.getApplicationContext());
			if (result) {
				for (LogRecord record : recordsToUpload) {
					databaseManager.purgeRecord(record.getId());
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Couldn't upload log.");
		}
	}
	
	public static boolean uploadLogs(List<LogRecord> recordsToUpload, Context context) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(LOG_COUNT, Integer.toString(recordsToUpload.size()));
		for (int i = 0; i < recordsToUpload.size(); i++) {
			LogRecord record = recordsToUpload.get(i);
			// TODO device later on identify device not just the user
			params.put(i + " " + LogRecord.COLUMN_NAME_MODULE, record.getModule());
			params.put(i + " " + LogRecord.COLUMN_NAME_LOG_LEVEL, record.getLogLevel());
			params.put(i + " " + LogRecord.COLUMN_NAME_DATE, Long.toString(record.getDate()));
			params.put(i + " " + LogRecord.COLUMN_NAME_MESSAGE, record.getMessage());
		}
		HttpUtils.post(ServerUtilities.SERVER_URL + "uploadLog", params, context);
		// TODO check response
		return false;
	}

	@Override
	public void onDestroy() {
		databaseManager.destroy();
		super.onDestroy();
	}
}
