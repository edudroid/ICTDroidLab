package hu.edudroid.module;

import hu.edudroid.interfaces.ThreadSemaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ModuleSemaphore implements ThreadSemaphore {
	
	private final Semaphore threadsemaphore = new Semaphore(1);
	private int threadid = 0;
	private List<Float> threadcpubuffer = new ArrayList<Float>(15);

	@Override
	public void aquirePermit() {
		// TODO Auto-generated method stub
		try {
			threadsemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void releasePermit() {
		// TODO Auto-generated method stub
		threadsemaphore.release();
	}

	@Override
	public int availablePermits() {
		// TODO Auto-generated method stub
		return threadsemaphore.availablePermits();
	}

	@Override
	public void setThreadId() {
		// TODO Auto-generated method stub
		if(threadid == 0){
		threadid = android.os.Process.myTid();
		}
	}

	@Override
	public int getThreadId() {
		// TODO Auto-generated method stub
		return threadid;
	}

	@Override
	public void addtoList(float item) {
		// TODO Auto-generated method stub
		threadcpubuffer.add(item);
	}

	@Override
	public void removefromList() {
		// TODO Auto-generated method stub
		List<Float> templist = new ArrayList<Float>(15);
		for(int i= 0; i < 14; i++){
			templist.add(threadcpubuffer.get(i+1));
		}
		threadcpubuffer = templist;
	}

	@Override
	public int sizeofList() {
		// TODO Auto-generated method stub
		return threadcpubuffer.size();
	}

	@Override
	public float getObjectfromList(int index) {
		// TODO Auto-generated method stub
		return threadcpubuffer.get(index);
	}

}
