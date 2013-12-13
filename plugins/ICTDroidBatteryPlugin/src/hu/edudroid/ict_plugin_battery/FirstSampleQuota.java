package hu.edudroid.ict_plugin_battery;

import hu.edudroid.interfaces.Quota;

public class FirstSampleQuota implements Quota{

	@Override
	public int getQuotaIdentifier() {
		return 1;
	}

	@Override
	public String getQuotaMeasurement() {
		return "Sample quota measurement";
	}

	@Override
	public int[] getQuotaLevels() {
		return new int[] {1000, 5000, 10000};
	}

}
