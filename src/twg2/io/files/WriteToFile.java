package twg2.io.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

public class WriteToFile {

	/** WriteToFile Constructor
	 * @param file - the file to write to
	 * @param generator - the event object to call of each string of input to write to the file, by casting the
	 * event object's getSource() return value to a String
	 */
	public WriteToFile(File file, Supplier<String> generator) {
		if(!file.canRead()) { throw new ExceptionInInitializerError("error reading file: " + file); }

		String lineSeperator = System.getProperty("line.separator");
		FileWriter outputWriter = null;
		String inputLine = null;
		//int fileCounter = 0;

		try {
			outputWriter = new FileWriter(file);
			while((inputLine = (String)generator.get()) != null) {
				//fileCounter++;
				outputWriter.append(inputLine + lineSeperator);
			}
			outputWriter.close();
		} catch (FileNotFoundException e) {
			System.err.println("could not find file: " + file);
			e.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("error reading file: " + file);
			ioe.printStackTrace();
		}
		finally {
			try {
				outputWriter.close();
			} catch (IOException e) {
				System.err.println("error closing file: " + file);
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) throws IOException {
		/*
		Scanner input = new Scanner(System.in);
		String fileName = null;

		System.out.print("Enter the file name and path: ");
		fileName = input.nextLine();

		File file = new File(fileName);
		if(!file.exists()) {
			file.createNewFile();
		}
		StringGenerator sg = new StringGenerator() {
			int aLetter = 0;
			int bLetter = 0;
			int cLetter = 0;
			public String nextString() {
				if(cLetter > 25) {
					bLetter++;
					cLetter = 0;
				}
				if(bLetter > 25) {
					aLetter++;
					bLetter = 0;
				}
				if(aLetter > 25) {
					return null;
				}
				cLetter++;
				return "" + (char)(aLetter + 65) + (char)(bLetter + 65) + (char)(cLetter-1 + 65);
			}
		};
		new WriteToFile(file, sg);
		*/
	}

}
