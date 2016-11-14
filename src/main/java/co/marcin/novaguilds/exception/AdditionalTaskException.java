package co.marcin.novaguilds.exception;

public class AdditionalTaskException extends Exception {
	/**
	 * The constructor
	 */
	public AdditionalTaskException() {

	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 */
	public AdditionalTaskException(String message) {
		super(message);
	}

	/**
	 * The constructor
	 *
	 * @param message exception message
	 * @param cause   cause
	 */
	public AdditionalTaskException(String message, Throwable cause) {
		super(message, cause);
	}
}
