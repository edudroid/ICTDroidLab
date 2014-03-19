package hu.edudroid.ict.logs.test;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.test.mock.MockContext;

public class MockIntentContext extends MockContext {
	
	List<Intent> startServiceIntents = new ArrayList<Intent>();
	
	@Override
	public String getPackageName() {
		return MockIntentContext.class.getPackage().getName();
	}
	
	@Override
	public ComponentName startService(Intent intent) {
		startServiceIntents.add(intent);
		return null;
	}
	
	public List<Intent> getStartServiceIntents() {
		return new ArrayList<Intent>(startServiceIntents);
	}
}