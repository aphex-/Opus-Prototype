package com.nukethemoon.tools.opusproto.log;


public class StandardOut implements Log.Out {

	@Override
	public void logError(String tag, String message) {
		System.out.print("Error: " + tag + " " + message);
	}

	@Override
	public void logInfo(String tag, String message) {
		System.out.print("Info: " + tag + " " + message);
	}

	@Override
	public void logDebug(String tag, String message) {
		System.out.print("Debug: " + tag + " " + message);
	}
}
