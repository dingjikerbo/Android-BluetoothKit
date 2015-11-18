package com.inuker.bluetooth.security.encryption;

/**
 * A custom exception for ecoding.
 * @author NoTiCe
 *
 */
public class EncryptionException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4405557080464242482L;

	/**
     * Inherited from {@link Exception}.
     */
    public EncryptionException() {
        super();
    }

    /**
     * Inherited from {@link Exception}.
     * @param message The message.
     * @param cause The root cause.
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Inherited from {@link Exception}.
     * @param message The message.
     */
    public EncryptionException(String message) {
        super(message);
    }

    /**
     * Inherited from {@link Exception}.
     * @param cause The root cause.
     */
    public EncryptionException(Throwable cause) {
        super(cause);
    }

}
