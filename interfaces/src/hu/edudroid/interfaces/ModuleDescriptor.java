package hu.edudroid.interfaces;

public class ModuleDescriptor implements Comparable<ModuleDescriptor> {
	private final String moduleName;
	private final String className;
	private final String jarFile;
	
	public ModuleDescriptor(String moduleName, String className, String jarFile) {
		this.moduleName = moduleName;
		this.jarFile = jarFile;
		this.className = className;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getClassName() {
		return className;
	}

	public String getJarFile() {
		return jarFile;
	}
	
	@Override
	public int compareTo(ModuleDescriptor another) {
		if (moduleName == null) {
			return -1;
		}
		return moduleName.compareTo(another.getModuleName());
	}
}
