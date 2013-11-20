package hu.edudroid.ict;

import hu.edudroid.interfaces.ModuleDescriptor;

import java.util.Map;

public interface ModuleSetListenerWithStats extends ModuleSetListener{
	void moduleSTatsChanged(ModuleDescriptor moduleDescriptor, Map<String, String> stats);
}
