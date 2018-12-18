package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import cleanjavacode.FilterManager;

/**
 * Provides static methods to do general things.
 * @author simone
 */

public class Utils {
	private static final String ENCODING = "UTF-8";
	//public static final Debugger debugger = new Debugger();

	/**
	 * Loads all the snippets.
	 * @return List of snippets
	 */
	/*public static List<Snippet> loadSnippets() {
		List<Snippet> snippets = new ArrayList<Snippet>();
		for (int i = 1; i<=100; i++) {
			try {
				snippets.add(new Snippet(String.valueOf(i), Utils.readFile("Snippets/"+String.valueOf(i)+".jsnp")));
			} catch (IOException e) {
				System.out.println("Could not load snippet " + i);
			}
		}
		return snippets;
	}*/
	
	/**
	 * Does nothing, returns pAvg!
	 * @param pAvg
	 * @param pMin
	 * @param pMax
	 * @return
	 */
	public static double normalize(double pAvg, double pMin, double pMax) {
		return pAvg;
//		if (pMax == pMin)
//			return 0;
//		else
//			return (pAvg-pMin)/(pMax-pMin);
	}
	
	public static double mirrorNormalize(double pAvg, double pMin, double pMax) {
		return pAvg;
//		if (pMax == pMin)
//			return 0;
//		else
//			return 1 - (pAvg-pMin)/(pMax-pMin);
	}
	
	/**
	 * Returns the content of the file with the specified path.
	 * @param pFilename The path of the file
	 * @return The content of the file
	 */
	public static String readFile(String pFilename) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(pFilename));
		return Charset.forName(ENCODING).decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public static boolean arrayContains(Object[] pArray, Object pContent) {
		for (int i = 0; i < pArray.length; i++) {
			if (pArray[i].equals(pContent))
				return true;
		}
		return false;
	}
	
	/**
	 * Writes the specified content to the file with the specified path.
	 * @param pFilename The path of the file
	 * @param pContent The content to write on the file
	 */
	public static void writeFile(String pFilename, String pContent) throws IOException {
		PrintWriter writer = new PrintWriter(pFilename, ENCODING);
		writer.print(pContent);
		writer.close();
	}
	
	public static void appendFile(String pFilename, String pContent) throws IOException {
		String content = readFile(pFilename);
		writeFile(pFilename, content + pContent);
	}
	
	/**
	 * Returns an array with all information about the given natural language text.
	 * @param pText Text to be analyzed
	 * @return An array with (in order) number of syllables, number of words and
	 * number of sentences of the given text.
	 */
	public static int[] getNLTextInfo(String pText) {
		String text = pText;
		text = FilterManager.applyMultipleSpacesFilter(text);
		text = FilterManager.applySymbolsFilters(text);
		int counter = 0;
		int words = 0; //set to one to account for last word
		int sentences = 0;
		int syllables = 0;
		boolean stopWordsIncrement = false;
		boolean stopSyllablesIncrement = false;
		while( counter < text.length()) {
			char currentChar = text.charAt(counter);
			char nextChar;
			if (counter + 1 < text.length())
				nextChar = text.charAt(counter+1);
			else
				nextChar = 0;
			
			switch (currentChar) {
				case '.':
				case '!':
				case '?':
				case ':':
				case ';':
					if (!stopWordsIncrement)
						words++;
					
					stopWordsIncrement = true;
					if (nextChar == ' ' || nextChar == '\t' || nextChar == '\n')
						sentences++;
					
					stopSyllablesIncrement 	= false;
	                break;
				case ' ':
					if (!stopWordsIncrement)
						words++;
					
					stopSyllablesIncrement 	= false;
					break;
				case 'a':
				case 'i':
				case 'o':
				case 'u':
				case 'y':
				case 'e':
					if (currentChar == 'e')
						if ((nextChar < 'a' || nextChar > 'z') && (nextChar < 'A' || nextChar > 'Z'))
							break;
					
					if (!stopSyllablesIncrement)
						syllables++;
					
					stopSyllablesIncrement 	= true;
					break;
				default:
					stopSyllablesIncrement 	= false;
					stopWordsIncrement 		= false;
					break;
			}
			counter++;
		}
		
		if (!stopWordsIncrement)
			words++;
		
		if (!stopSyllablesIncrement)
			syllables++;
		if (counter != 0) {
			char lastChar = text.charAt(counter-1);
			if (lastChar != ' ' && lastChar != '\t' && lastChar != '\n')
				sentences++;
		}
		
		int[] result = {syllables, words, sentences};
		return result;
	}
	
	public static List<String[]> makeSortedCombinations(String[] pList) {
		List<String[]> combinations = new ArrayList<String[]>();
		
		for (int i = 0; i < pList.length; i++)
			for (int j = i + 1; j < pList.length; j++) {
				combinations.add(new String[] {pList[j], pList[i]});
			}
		
		return combinations;
	}
}
