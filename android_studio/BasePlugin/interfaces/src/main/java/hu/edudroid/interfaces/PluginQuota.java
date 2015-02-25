package hu.edudroid.interfaces;

public abstract class PluginQuota {

	private final String	mMethodName;
	private final int[]		mQuota;
	private final long[]	mQuotaInterval;

	public enum QuotaType {
		QUOTA_STRICT, QUOTA_MEDIUM, QUOTA_LIGHT
	};

	public PluginQuota(final String methodName, final QuotaType type) {
		mMethodName = methodName;
		mQuota = getQuota();
		mQuotaInterval = getQuotaInterval();
	}

	public final boolean validateQuota(final String method, final QuotaType type){
		if (!method.equals(mMethodName))
			return true;

		return validateQuota(mQuota[type.ordinal()], mQuotaInterval[type.ordinal()]);
	}

	protected abstract int[] getQuota();

	protected abstract long[] getQuotaInterval();

	protected abstract boolean validateQuota(	final int quota,
												final long quotaInterval);
}