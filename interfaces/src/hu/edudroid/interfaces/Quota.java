package hu.edudroid.interfaces;

public interface Quota {

	int getQuotaIdentifier();
	String getQuotaMeasurement();
	int[] getQuotaLevels();
}
