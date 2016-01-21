package com.nukethemoon.tools.simpletask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The SimpleTaskExecutor simply executes tasks on different threads
 * and pass the result back to the origin thread.
 */
public class SimpleTaskExecutor<T> {

	// maps a task to a result listener.
	private Map<Callable<T>, ResultListener<T>> tasksToResult;

	// maps a executing task (Future) to a result listener.
	private Map<Future<T>, ResultListener<T>> futureToListener;

	// The service to execute tasks on.
	private ExecutorService service;

	private boolean executing = false;

	/**
	 * Creates an new instance using the available processors as thread count.
	 */
	public SimpleTaskExecutor() {
		this(Runtime.getRuntime().availableProcessors());
	}

	/**
	 * Creates an new instance.
	 * @param threadCount The count of threads to use.
	 */
	public SimpleTaskExecutor(int threadCount) {
		service = Executors.newFixedThreadPool(threadCount);
		tasksToResult = new HashMap<Callable<T>, ResultListener<T>>();
		futureToListener = new HashMap<Future<T>, ResultListener<T>>();
	}

	/**
	 * Creates an new instance.
	 * @param threadCount The count of threads to use.
	 * @param proprity The execution priority of the threads.
	 */
	public SimpleTaskExecutor(int threadCount, final int priority) {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread();
				thread.setPriority(priority);
				return thread;
			}
		};

		service = Executors.newFixedThreadPool(threadCount, threadFactory);
		tasksToResult = new HashMap<Callable<T>, ResultListener<T>>();
		futureToListener = new HashMap<Future<T>, ResultListener<T>>();
	}

	/**
	 * Executes the added tasks.
	 * Can only be called once for an instance.
	 * @return True if execution was done.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public boolean execute() throws ExecutionException, InterruptedException {
		if (service.isShutdown() || tasksToResult.size() == 0 || executing) {
			return false;
		}
		executing = true;

		// start all tasks ...
		List<Future<T>> futures = new ArrayList<Future<T>>();
		for (Map.Entry<Callable<T>, ResultListener<T>> entry : tasksToResult.entrySet()) {
			Future<T> future = service.submit(entry.getKey());
			futureToListener.put(future, entry.getValue());
			futures.add(future);
		}

		// wait until tasks are done ...
		List<Future<T>> doneList = new ArrayList<Future<T>>();
		while (futures.size() > 0) {

			// check if it is done
			for (Future<T> precessingFuture : futures) {
				if (precessingFuture.isDone()) {
					doneList.add(precessingFuture);
				}
			}

			// remove all done tasks and call the result listeners
			for (Future<T> doneFuture : doneList) {
				futures.remove(doneFuture);
				ResultListener<T> listener = futureToListener.get(doneFuture);
				if (listener != null) {
					listener.onResult(doneFuture.get());
				}
			}
			doneList.clear();
		}

		// cleanup
		futures.clear();
		tasksToResult.clear();
		futureToListener.clear();
		service.shutdown();
		executing = false;
		return true;
	}

	/**
	 * Adds a task to the queue.
	 * @param task A the task to execute.
	 * @param resultListener A listener to call if the task finished.
	 * @return This instance.
	 */
	public SimpleTaskExecutor<T> addTask(Callable<T> task, ResultListener<T> resultListener) {
		tasksToResult.put(task, resultListener);
		return this;
	}

	/**
	 * An interface to listen on a result.
	 * @param <T> The result.
	 */
	public static interface ResultListener<T> {
		void onResult(T result);
	}
}