package hu.edudroid.module;


import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class ModuleTimeService implements TimeServiceInterface {
	
	private static ModuleTimeService	mInstance	= null;
	
	Timer t=new Timer();
	
	public static ModuleTimeService getInstance(){
		if (mInstance == null){
			synchronized (ModuleTimeService.class){
				if (mInstance == null)
					mInstance = new ModuleTimeService();
			}
		}
		return mInstance;
	}
	
	public void runAt(final int delay, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			
			@Override
			public void run() {
				listener.onTimerEvent();
			}
		};
		
		t.schedule(tTask, delay);
	}
	
	public void runAt(final Date when, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			
			@Override
			public void run() {
				listener.onTimerEvent();
			}
		};
		
		t.schedule(tTask, when);
	}
	
	public void runPeriodic(final int delay, final int periodicity, final int tickCount, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			int count=tickCount;
			
			@Override
			public void run() {
				if(count==0){
					t.purge();
				}
				else{
					listener.onTimerEvent();
					count--;
				}
			}
		};
		
		t.scheduleAtFixedRate(tTask, delay, periodicity);
	}
	
	public void runPeriodic(final Date when, final int periodicity, final int tickCount, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			int count=tickCount;
			
			@Override
			public void run() {
				if(count==0){
					t.purge();
				}
				else{
					listener.onTimerEvent();
					count--;
				}
			}
		};
		
		t.scheduleAtFixedRate(tTask, when, periodicity);
	}
	
}
