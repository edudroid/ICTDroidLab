package hu.edudroid.ict.ui;

public class ModuleDescriptor extends hu.edudroid.interfaces.ModuleDescriptor {

	private boolean loaded;

	public ModuleDescriptor(String moduleName, String className, String jarFile, boolean loaded) {
		super(moduleName, className, jarFile);
		this.loaded = loaded;
	}

	public ModuleDescriptor(hu.edudroid.interfaces.ModuleDescriptor descriptor, boolean loaded) {
		super(descriptor.getModuleName(), descriptor.getClassName(), descriptor.getJarFile());
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}

}
