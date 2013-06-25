package hu.edudroid.ictpluginwifi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PluginMethod {

	private Method			mMethod;
	private PluginLogic		mPlugin;

	public final String		mName;
	public final String		mDescription;

	public PluginMethod(final String methodName,
						final String description,
						final PluginLogic plugin) {
		mName = methodName;
		mDescription = description;

		mPlugin = plugin;
		final Method[] methods = PluginLogic.class.getMethods();
		for (int i = 0; i < methods.length; i++)
			if (methods[i].getName().equals(mName))
				mMethod = methods[i];
	}

	public void invoke(Object[] params)	throws InvocationTargetException,
										IllegalArgumentException,
										IllegalAccessException{
		mMethod.invoke(mPlugin, params);
	}
}
