package hu.edudroid.ict;

import java.util.Map;

public interface ModuleStatsListener {
	public static final String STAT_KEY_TIMERS_FIRED = "Timer events fired";
	public static final String STAT_KEY_LAST_TIMER_EVENT = "Last timer event";
	
	void moduleStatsChanged(String moduleId, Map<String, String> stats);
}