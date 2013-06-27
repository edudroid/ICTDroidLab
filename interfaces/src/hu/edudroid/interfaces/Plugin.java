package hu.edudroid.interfaces;

import java.util.List;

public interface Plugin {

	String getAuthor();
	String getName();
	String getDescription();
	String getVersionCode();
	List<String> getMethodsName();
	void callMethod(String method, List<Object> parameters);
}
