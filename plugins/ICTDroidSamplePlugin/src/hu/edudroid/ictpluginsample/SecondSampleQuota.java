package hu.edudroid.ictpluginsample;

import hu.edudroid.interfaces.Quota;

public class SecondSampleQuota implements Quota{

	@Override
	public int getQuotaIdentifier() {
		return 2;
	}

	@Override
	public String getQuotaMeasurement() {
		return "Second sample quota measurement";
	}

	@Override
	public int[] getQuotaLevels() {
		return new int[] {10, 200, 5000};
	}
}
