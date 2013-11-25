package hu.edudroid.ict;

import hu.edudroid.module.ModuleDescriptor;

public interface ModuleSetListener {
	void moduleAdded(ModuleDescriptor moduleDescriptor);
	void moduleRemoved(ModuleDescriptor moduleDescriptor);
}