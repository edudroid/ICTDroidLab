package hu.edudroid.interfaces;

public interface Plugin {

	void callMethod(PluginCall call);
	String getAuthor();
	String getName();
	String getDescription();
	String getVersionCode();

}
