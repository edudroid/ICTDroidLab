package hu.edudroid.ict.sample_project2;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;
import hu.edudroid.interfaces.ThreadSemaphore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
public class ModulExample2 extends Module {
  SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
  public ModulExample2(  Preferences prefs,  Logger logger,  PluginCollection pluginCollection,  TimeServiceInterface timeservice,  ThreadSemaphore threadsemaphore){
    super(prefs,logger,pluginCollection,timeservice,threadsemaphore);
  }
  private static final String TAG="ModuleExample2";
  @Override public void init(){
    mLogger.e(TAG,"Module init...");
    mTimeService.runPeriodic(1000,5000,0,this);
  }
  public void run(){
	    setTid();
	    threadSleeper();
	    while (true) {
	      threadSleeper();
	    }
	  }
  
  @Override public void onResult(  long id,  String plugin,  String pluginVersion,  String methodName,  List<String> result){
  }
  @Override public void onError(  long id,  String plugin,  String pluginVersion,  String methodName,  String errorMessage){
  }
  @Override public void onEvent(  String plugin,  String version,  String eventName,  List<String> extras){
  }
  @Override public void onTimerEvent(){
    setTid();
    threadSleeper();
    mLogger.d(TAG,"timer event");
    mLogger.i(TAG,"Module example run at " + dateFormatter.format(new Date()));
    this.run();
  }
  public void setTid(){
    mThreadSemaphore.setThreadId();
  }
  public void threadSleeper(){
    if (mThreadSemaphore.availablePermits() == 0) {
      long time=500;
      try {
		Thread.sleep(time);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  }
}
