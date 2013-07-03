package hu.edudroid.module;


import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.util.List;

public class ModuleTimeService implements TimeServiceInterface {
	
	private static ModuleTimeService	mInstance	= null;
	List<ModuleTimerListener> mListeners;
	
	public static ModuleTimeService getInstance(){
		if (mInstance == null){
			synchronized (ModuleTimeService.class){
				if (mInstance == null)
					mInstance = new ModuleTimeService();
			}
		}
		return mInstance;
	}
	
	public void registerOnTimerEvent(ModuleTimerListener listener){
		mListeners.add(listener);
	}
	
	public void runAt(long time){
		
	}
	
	public void runPeriodic(long start, long period){
		
	}
	
}
