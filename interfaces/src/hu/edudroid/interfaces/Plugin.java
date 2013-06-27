package hu.edudroid.interfaces;

import java.util.List;

public interface Plugin {

	String getAuthor();
	String getName();
	String getDescription();
	String getVersionCode();
	void callMethod(String method, List<Object> parameters);
}
