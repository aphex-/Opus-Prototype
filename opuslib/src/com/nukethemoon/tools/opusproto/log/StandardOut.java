package com.nukethemoon.tools.opusproto.log;


public class StandardOut implements Log.Out {

	@Override
	public void logError(String tag, String message) {
		System.out.println("Error: " + tag + " " + message);
	}

	@Override
	public void logInfo(String tag, String message) {
		System.out.println("Info: " + tag + " " + message);
	}

	@Override
	public void logDebug(String tag, String message) {
		System.out.println("Debug: " + tag + " " + message);
	}
}
