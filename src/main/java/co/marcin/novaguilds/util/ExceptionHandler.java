package co.marcin.novaguilds.util;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		LoggerUtils.exception(throwable);
	}
}
