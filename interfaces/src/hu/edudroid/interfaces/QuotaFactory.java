package hu.edudroid.interfaces;

public class QuotaFactory {

	private QuotaFactory() {
		// Utility class
	}

	public static String codeQuota(final Quota quota){
		String descr = quota.getQuotaIdentifier()
						+ "|"
						+ quota.getQuotaMeasurement()
						+ "|";
		final int levelsCount = quota.getQuotaLevels().length;
		for (int i = 0; i < levelsCount; i++)
			descr += quota.getQuotaLevels()[i]
						+ ((i != levelsCount - 1) ? "|" : "");
		
		return descr;
	}
	
	public static Quota decodeQuota(final String descr){
		final String[] partitions = descr.split("|");
		
		Quota quota = new Quota() {
			
			@Override
			public String getQuotaMeasurement(){
				return partitions[1];
			}
			
			@Override
			public int[] getQuotaLevels(){
				final int levels[] = new int[partitions.length - 2];
				for (int i = 0; i < levels.length; i++)
					levels[i] = Integer.parseInt(partitions[i + 2]);
				return levels;
			}
			
			@Override
			public int getQuotaIdentifier(){
				return Integer.parseInt(partitions[0]);
			}
		};
		
		return quota;
	}
}
