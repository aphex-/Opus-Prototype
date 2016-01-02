package com.nukethemoon.tools.opusproto.log;

public class Log {

	public static Out out = new StandardOut();
	public static LogType logLevel = LogType.Info;

	public static void i(Class source, String message) {
		if (out != null && logLevel == LogType.Info) {
			out.logInfo(source.getSimpleName(), message);
		}
	}
	public static void d(Class source, String message) {
		if (out != null && (logLevel == LogType.Info || logLevel == LogType.Debug)) {
			out.logDebug(source.getSimpleName(), message);
		}
	}
	public static void e(Class source, String message) {
		if (out != null) {
			out.logError(source.getSimpleName(), message);
		}
	}

	public interface Out {
		void logError(String tag, String message);
		void logInfo(String tag, String message);
		void logDebug(String tag, String message);
	}

	public enum LogType {
		Error,
		Info,
		Debug
	}
}
