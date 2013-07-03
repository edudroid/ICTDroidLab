package hu.edudroid.interfaces;

import java.util.Date;

public interface TimeServiceInterface {
	public void runAt(int delay, ModuleTimerListener listener);
	public void runAt(Date when, ModuleTimerListener listener);
	public void runPeriodic(int delay, int periodicity, int tickCount, ModuleTimerListener listener);
	public void runPeriodic(Date when, int periodicity, int tickCount, ModuleTimerListener listener);
}
