package hu.edudroid.ict.plugins;

public class PluginMethod implements Comparable<PluginMethod> {

	private final int	mOrder;
	public final String	mName;
	public final String	mDescription;

	public PluginMethod(final int order,
						final String methodName,
						final String description) {
		mOrder = order;
		mName = methodName;
		mDescription = description;
	}

	public int getOrder(){
		return mOrder;
	}

	@Override
	public int compareTo(PluginMethod another){
		return (Integer.valueOf(mOrder)).compareTo(Integer.valueOf(another.getOrder()));
	}
}