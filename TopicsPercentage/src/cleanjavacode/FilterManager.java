package cleanjavacode;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cleanjavacode.CodeAnalyzer;
import util.Utils;

/**
 * Contains only static methods. Each method provide a specific text filter.
 * @author simone
 */
public class FilterManager {
	/**
	 * Removes all non-word characters from the given string
	 * @param pSource Source text
	 * @return List of words separated by " "
	 */
	public static String applyNonWordFilter(String pSource) {
		//String onlyWords = pSource.replaceAll("[^\\p{L}\\p{Nd}\\_]", " ");
                
//                 String onlyWords = workaroundNonWordFilter(pSource);
//                 FilterManager.applyMultipleSpacesFilter(onlyWords).replaceAll("[^A-Za-z_$]\\d", " ");
		Pattern tokenDetectorPattern = Pattern.compile("([A-Za-z_$][A-Za-z_$0-9]*)");
		Matcher matcher = tokenDetectorPattern.matcher(pSource);

		String resultString = "";
		while (matcher.find()) {
			resultString += matcher.group() + " ";
		}

		return resultString.trim();
	}
	
	/**
	 * Changes all multiple spaces in a single space.
	 * @param pSource Source text
	 * @return Source text filtered
	 */
	public static String applyMultipleSpacesFilter(String pSource) {
		return pSource.replaceAll(" +(?= )","");
	}
	
	/**
	 * Removes all standard stop words from the given source.
	 * @param pSource Source text
	 * @return Source text filtered
	 */
	public static String applyStopWordFilter(String pSource) {
		String[] stopWords = null;
		
		try {
			stopWords = Utils.readFile("stopwords").split("\n");
			return applyStopWordFilter(pSource, stopWords);
		} catch (IOException e) {
		}
		
		return "";
	}
	
	/**
	 * Removes all given stop words from the source.
	 * @param pSource Source text
	 * @param pStopWords List of stop words to be removed from the source
	 * @return Source text filtered
	 */
	public static String applyStopWordFilter(String pSource, String[] pStopWords) {
		String result = pSource;
		for (int i = 0; i < pStopWords.length; i++) {
			result = pSource.replaceAll(CodeAnalyzer.getSingleWordRegex(pStopWords[i]), "");
		}
		return result;
	}

	/**
	 * Removes every character that is not a letter, a number, a punctuation symbol,
	 * a space or a newline character.
	 * @param pSource Source text
	 * @return Source text filtered
	 */
	public static String applySymbolsFilters(String pSource) {
		return pSource.replaceAll("[^\\p{L}\\p{Nd}\\p{P} \n]", "");
	}
	
	/**
	 * Returns the given source without blank lines.
	 * @param pSource Source
	 * @return Filtered Source
	 */
	public static String deleteBlankLines(String pSource) {
		String result = "";
		String source = pSource.replaceAll("\t", "");
		while (source.length() != 0) {
			if (!source.startsWith("\n")) {
				int index = source.indexOf("\n")+1;
				if (index == 0)
					index = source.length(); //Index of the word
				
				result += source.substring(0, index); //Adds the part to the result
				source = source.substring(index); //Erases the added part of the string
			} else
				source = source.substring(1); //Erases the "\n"
		}
		
		return result;
	}
	
	private static String workaroundNonWordFilter(String pSource) {
		String result = "";
		for (int i = 0; i < pSource.length();i++) {
			char current = pSource.charAt(i);
			
			if ((current >= 'A' && current <= 'Z') ||
					(current >= 'a' && current <= 'z') ||
					(current >= '0' && current <= '9') ||
					(current == '_') ||
					(current == ' ') ||
					(current == '\n'))
				result += current;
			else
				result += " "; //FIXFIXFIX
		}
		
		return result;
	}
}
