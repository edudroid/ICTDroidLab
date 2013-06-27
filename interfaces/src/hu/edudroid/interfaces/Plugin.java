package hu.edudroid.interfaces;

import java.util.List;

public interface Plugin {
	void callMethod(String method, List<Object> parameters);
}
