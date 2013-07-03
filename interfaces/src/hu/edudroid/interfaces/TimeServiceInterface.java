package hu.edudroid.interfaces;

public interface TimeServiceInterface {
	public void registerOnTimerEvent(ModuleTimerListener listener);
	public void runAt(long time);
	public void runPeriodic(long start, long period);
}
