package hu.edudroid.interfaces;

import java.util.Date;

public interface TimeServiceInterface {
	public void cancelAll();
	public void runAt(int delay, ModuleTimerListener listener);
	public void runAt(Date when, ModuleTimerListener listener);

	/**
	 * Runs periodically starting from delay.
	 * @param delay The delay in millis until the first run.
	 * @param periodicity The time in millis between two runs.
	 * @param tickCount The number of runs. If 0, runs indefinitely.
	 * @param listener The listener who will be woken up.
	 */
	public void runPeriodic(int delay, int periodicity, int tickCount, ModuleTimerListener listener);

	/**
	 * Runs periodically starting at a given date.
	 * @param when The date of the first run.
	 * @param periodicity The time in millis between two runs.
	 * @param tickCount The number of runs. If 0, runs indefinitely.
	 * @param listener The listener who will be woken up.
	 */
	public void runPeriodic(Date when, int periodicity, int tickCount, ModuleTimerListener listener);
}
