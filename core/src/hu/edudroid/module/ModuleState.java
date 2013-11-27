package hu.edudroid.module;

import android.content.Context;
import hu.edudroid.ict.R;

public enum ModuleState {
	AVAILABLE(0, R.string.moduleStateAvailable), INSTALLED(1, R.string.moduleStateInstalled), TERMINATED(2, R.string.moduleStateTerminated), BANNED(3, R.string.moduleStateBanned);
	private int stateValue;
	private int stateString;

	private ModuleState(int stateValue, int stateString) {
		this.stateValue = stateValue;
		this.stateString = stateString;
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
		return "State_" + stateString;
	}

	public String toString(Context context) {
		return context.getString(stateString);
	}

	public int getValue() {
		return stateValue;
	}
}