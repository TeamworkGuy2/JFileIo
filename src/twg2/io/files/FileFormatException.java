package twg2.io.files;

public class FileFormatException extends Exception {
	private static final long serialVersionUID = 0000201300070014L;


	/** IllegalFormatException, because java does not have one
	 * @param message the detail message
	 */
	public FileFormatException(String message) {
		super(message);
	}


	/** IllegalFormatException, because java does not have one
	 * @param cause the error cause
	 */
	public FileFormatException(Throwable cause) {
		super(cause);
	}


	/** IllegalFormatException, because java does not have one
	 * @param message the detail message
	 * @param cause the error cause
	 */
	public FileFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
