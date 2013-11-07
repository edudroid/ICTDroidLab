package hu.edudroid.ict;

import hu.edudroid.interfaces.ModuleDescriptor;

public interface ModuleSetListener {

	void moduleAdded(ModuleDescriptor moduleDescriptor);
	void moduleRemoved(ModuleDescriptor moduleDescriptor);
	
}
