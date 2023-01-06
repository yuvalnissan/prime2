package ai.prime.common.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Lock {
	private static final long TIMEOUT = 1000;
	
	private final ReentrantLock lock = new ReentrantLock();

	public void lock(){
		try{
			if (!lock.tryLock(TIMEOUT, TimeUnit.MILLISECONDS)) {
				System.out.println("Deadlock!");
			}
		} catch(Exception e) {
			throw new RuntimeException("Failed to acquire lock!", e);
		}
	}

	public void unlock() {
		lock.unlock();
	}

    public boolean isLocked() {
        return lock.isLocked();
    }
}
