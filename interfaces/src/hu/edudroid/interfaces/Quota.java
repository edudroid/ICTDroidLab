package hu.edudroid.interfaces;

public class Quota {

	private long quotaIdentifier;
	private String quotaName;
	private int[] quotaLevels;
	private long regenerationPeriod;
	
	public Quota(long quotaIdentifier, String quotaName, long regenerationPeriod, int[] quotaLevels) {
		this.quotaIdentifier = quotaIdentifier;
		this.quotaName = quotaName;
		this.quotaLevels = quotaLevels;
	}
	
	public long getQuotaIdentifier() {
		return quotaIdentifier;
	}
	public String getQuotaName() {
		return quotaName;
	}
	public int[] getQuotaLevels() {
		return quotaLevels;
	}
	
	public long getRegenerationPeriod() {
		return regenerationPeriod;
	}

	public static String codeQuota(final Quota quota){
		String descr = quota.quotaIdentifier
						+ "|"
						+ quota.quotaName
						+ "|"
						+ quota.regenerationPeriod
						+ "|";
		final int levelsCount = quota.getQuotaLevels().length;
		for (int i = 0; i < levelsCount; i++)
			descr += quota.getQuotaLevels()[i]
						+ ((i != levelsCount - 1) ? "|" : "");
		
		return descr;
	}
	
	public static Quota decodeQuota(final String descr) throws QuotaException{
		final String[] parts = descr.split("|");
		if (parts.length < 3) {
			throw new QuotaException("Invalid argument " + descr);
		}
		int[] levels = new int[parts.length - 3];
		for (int i = 0; i < levels.length; i++) {
			try {
				levels[i] = Integer.parseInt(parts[i + 3]);
			} catch (Exception e) {
				e.printStackTrace();
				throw new QuotaException("Invalid argument " + descr);
			}
		}
		try {
			Quota quota = new Quota(Integer.parseInt(parts[0]),parts[1], Long.parseLong(parts[2]), levels);
			return quota;
		} catch (Exception e) {
			e.printStackTrace();
			throw new QuotaException("Invalid argument " + descr);
		}
	}
}
