package hu.edudroid.module;


import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ModuleTimeService implements TimeServiceInterface {
	
	Timer timer = new Timer();
	
	public void runAt(final int delay, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			
			@Override
			public void run() {
				listener.onTimerEvent();
			}
		};
		
		timer.schedule(tTask, delay);
	}
	
	public void runAt(final Date when, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			
			@Override
			public void run() {
				listener.onTimerEvent();
			}
		};
		
		timer.schedule(tTask, when);
	}
	
	public void runPeriodic(final int delay, final int periodicity, final int tickCount, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			int count=tickCount;
			
			@Override
			public void run() {
				if(count==0){
					timer.purge();
				}
				else{
					listener.onTimerEvent();
					count--;
				}
			}
		};
		
		timer.scheduleAtFixedRate(tTask, delay, periodicity);
	}
	
	public void runPeriodic(final Date when, final int periodicity, final int tickCount, final ModuleTimerListener listener){
		TimerTask tTask=new TimerTask() {
			int count=tickCount;
			
			@Override
			public void run() {
				if(count==0){
					timer.purge();
				}
				else{
					listener.onTimerEvent();
					count--;
				}
			}
		};
		
		timer.scheduleAtFixedRate(tTask, when, periodicity);
	}

	@Override
	public void cancelAll() {
		timer.cancel();
	}
	
}
