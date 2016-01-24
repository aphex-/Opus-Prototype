package com.nukethemoon.tools.simpletask;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class SimpleThreadFactory implements ThreadFactory {

	private final int threadPriority;
	private final boolean daemon;
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;


	public SimpleThreadFactory(int threadPriority, boolean daemon) {
		this.threadPriority = threadPriority;
		this.daemon = daemon;
		namePrefix = "pool-" +poolNumber.getAndIncrement() + "-thread-";

	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
		thread.setDaemon(daemon);
		thread.setPriority(threadPriority);
		return thread;
	}
}

