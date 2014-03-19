package hu.edudroid.ict.logs.test;

import java.util.List;

import hu.edudroid.ict.logs.AndroidLogger;
import hu.edudroid.ict.logs.LogRecord;
import hu.edudroid.ict.logs.UploadService;
import android.content.Intent;
import android.test.AndroidTestCase;

public class AndroidLoggerTest extends AndroidTestCase{
	
	public void testError() {
		MockIntentContext context = new MockIntentContext();
		AndroidLogger logger = new AndroidLogger("Test module", context);
		logger.e("Test tag", "Test message");
		List<Intent> intents = context.getStartServiceIntents();
		assertEquals(1, intents.size());
		
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MODULE), "Test module");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_LOG_LEVEL), "e");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message");
		assertEquals(intents.get(0).getComponent().getClassName(), UploadService.class.getName());
		
	}
	
	public void testInfo() {
		MockIntentContext context = new MockIntentContext();
		AndroidLogger logger = new AndroidLogger("Test module", context);
		logger.i("Test tag", "Test message");
		List<Intent> intents = context.getStartServiceIntents();
		assertEquals(1, intents.size());
		
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MODULE), "Test module");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_LOG_LEVEL), "i");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message");
		assertEquals(intents.get(0).getComponent().getClassName(), UploadService.class.getName());
		
	}
	public void testDebug() {
		MockIntentContext context = new MockIntentContext();
		AndroidLogger logger = new AndroidLogger("Test module", context);
		logger.d("Test tag", "Test message");
		List<Intent> intents = context.getStartServiceIntents();
		assertEquals(1, intents.size());
		
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MODULE), "Test module");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_LOG_LEVEL), "d");
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message");
		assertEquals(intents.get(0).getComponent().getClassName(), UploadService.class.getName());
		
	}
	
	public void testMessageOrder() {
		MockIntentContext context = new MockIntentContext();
		AndroidLogger logger = new AndroidLogger("Test module", context);
		logger.d("Test tag", "Test message 1");
		logger.e("Test tag", "Test message 2");
		logger.i("Test tag", "Test message 3");
		logger.d("Test tag", "Test message 4");
		List<Intent> intents = context.getStartServiceIntents();
		assertEquals(4, intents.size());
		
		assertEquals(intents.get(0).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message 1");
		assertEquals(intents.get(1).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message 2");
		assertEquals(intents.get(2).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message 3");
		assertEquals(intents.get(3).getStringExtra(LogRecord.COLUMN_NAME_MESSAGE), "Test message 4");
	}
}
