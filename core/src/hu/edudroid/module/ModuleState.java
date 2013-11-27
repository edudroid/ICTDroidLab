package hu.edudroid.module;

public enum ModuleState {
	AVAILABLE(0, "AVAILABLE"), INSTALLED(1, "INSTALLED"), TERMINATED(2, "TERMINATED"), BANNED(3, "BANNED");
	private int stateValue;
	private String stringValue;

	private ModuleState(int stateValue, String stringValue) {
		this.stateValue = stateValue;
		this.stringValue = stringValue;
	}
	
	public static ModuleState getModuleState(int stateValue){
		for (ModuleState state : ModuleState.values()) {
			if (state.stateValue == stateValue) {
				return state;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return stringValue;
	}

	public int getValue() {
		return stateValue;
	}
}