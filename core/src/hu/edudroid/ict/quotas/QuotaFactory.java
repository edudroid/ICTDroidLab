package hu.edudroid.ict.quotas;

import hu.edudroid.interfaces.Quota;


public class QuotaFactory {

	private QuotaFactory(){
		// Utility class
	}
	
	public static Quota createQuota(final int identifier,
	                                final String measurement,
	                                final int[] levels){
		return new Quota() {
			
			@Override
			public String getQuotaMeasurement(){
				return measurement;
			}
			
			@Override
			public int[] getQuotaLevels(){
				return levels;
			}
			
			@Override
			public int getQuotaIdentifier(){
				return identifier;
			}
		};
	}
	
}
