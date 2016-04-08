package twg2.io.files;

public class FileFormatException extends Exception {
	private static final long serialVersionUID = 0000201300070014L;


	/** FileFormatException, because java does not have one
	 * @param message the detail message
	 */
	public FileFormatException(String message) {
		super(message);
	}


	/** FileFormatException, because java does not have one
	 * @param cause the error cause
	 */
	public FileFormatException(Throwable cause) {
		super(cause);
	}


	/** FileFormatException, because java does not have one
	 * @param message the detail message
	 * @param cause the error cause
	 */
	public FileFormatException(String message, Throwable cause) {
		super(message, cause);
	}
	

	/** FileFormatException, because java does not have one
	 * @param fileName the name of the file which contained malformed data
	 * @param message the detail message
	 */
	public FileFormatException(String fileName, String message) {
		super("file '" + fileName + "': " + message);
	}


	/** FileFormatException, because java does not have one
	 * @param fileName the name of the file which contained malformed data
	 * @param message the detail message
	 * @param cause the error cause
	 */
	public FileFormatException(String fileName, String message, Throwable cause) {
		super("file '" + fileName + "'" + (message != null ? ": " + message : ""), cause);
	}
	
}
