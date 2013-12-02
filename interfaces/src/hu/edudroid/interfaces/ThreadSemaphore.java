package hu.edudroid.interfaces;

public interface ThreadSemaphore {
	
	void aquirePermit();
	
	void releasePermit();
	
	int availablePermits();
	
	void setThreadId();
	
	int getThreadId();
	
	void addtoList(float item);
	
	void removefromList();
	
	int sizeofList();
	
	float getObjectfromList(int index);

}
