package cleanjavacode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cleanjavacode.FilterManager;

public class JavaCodeAnalyzer extends CodeAnalyzer {
    
        public JavaCodeAnalyzer(){
            
        }
        
	public String deleteComments(String pSource) {
		return this.workaroundRemoveComments(this.deleteInlineComments(pSource));
	}
	
	public String deleteInlineComments(String pSource) {
		return pSource.replaceAll("(?://[^\\n]*\\n)","");
		//return pSource.replaceAll("//[^\\n]*\\n","");
	}
	
	public String deleteMultilineComments(String pSource) {
		//return pSource.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)","");
		//return pSource.replaceAll("/\\*([^*]|\\*+[^*])*\\*+/","");
		return this.workaroundRemoveMultilineComments(pSource);
	}
	
	public List<String> eachVisualLine(String pSource) {
		List<String> result = new ArrayList<String>();
		String source = pSource;
		String[] resultArr = source.split("\n");
		for (int i = 0; i < resultArr.length; i++)
			result.add(resultArr[i]);
		
		return result;
	}
	
	public String filterControlStructureWords(String pSource) {
		String statement = pSource;
		
		statement = statement.replaceAll(getSingleWordRegex("if"), "");
		statement = statement.replaceAll(getSingleWordRegex("else"), "");
		statement = statement.replaceAll(getSingleWordRegex("while"), "");
		statement = statement.replaceAll(getSingleWordRegex("switch"), "");
		statement = statement.replaceAll(getSingleWordRegex("try"), "");
		statement = statement.replaceAll(getSingleWordRegex("catch"), "");
		statement = statement.replaceAll(getSingleWordRegex("finally"), "");
		statement = statement.replaceAll("[\\p{Z}\\p{S}\\p{P}]for *\\(", "");
		statement = statement.replaceAll("case [\\w\\d\\+\\-\\/\\*\\(\\)\\&\\|\\,\\!\\\"\\'\\^]*\\:", "");
		
		return statement;
	}
	
	public List<String> eachStatementLine(String pSource) {
		List<String> result = new ArrayList<String>();
		String source = pSource;
		source = source.replaceAll("\n", "");
		source = source.replaceAll("}", "");
		String[] resultArr = source.split("\\;");
		for (int i = 0; i < resultArr.length; i++) {
			if (resultArr[i].contains("{")) {
				String[] resultArrTemp = resultArr[i].split("\\{");
				for (int j = 0; j < resultArrTemp.length; j++)
					result.add(this.filterControlStructureWords(resultArrTemp[j])+";");
			} else {
				result.add(resultArr[i]+";");
			}
		}
		return result;
	}
	
	public List<String> eachConstructLine(String pSource, List<Integer> pOccurrences) {
		String source = pSource;
		source = FilterManager.deleteBlankLines(source);
		source = source.replaceAll("\n", "");
		
		int openOccurrences = 0;
		int closeOccurrences = 0;
		int semicolonOccurrences = 0;
		
		List<String> result = new ArrayList<String>();
		String currentLine = "";
		boolean inQuotes = false;
		for (int i = 0; i < source.length(); i++) {
			char currentChar = source.charAt(i);
			char previousChar = 0;
			
			if (i > 0)
				previousChar = source.charAt(i-1);			
			
			if (currentChar == '{' || currentChar == ';' || currentChar == '}') {
				if (!inQuotes && previousChar != '\'') {
					currentLine += currentChar;
					result.add(currentLine);
					currentLine = "";
					switch (currentChar) {
					case '{':
						openOccurrences++;
						break;
					case '}':
						closeOccurrences++;
						break;
					case ';':
						semicolonOccurrences++;
						break;
					}
				}
			} else if (currentChar == '"') {
				if (inQuotes && previousChar != '\\')
					inQuotes = false;
				if (!inQuotes && previousChar != '\'')
					inQuotes = true;
				currentLine += currentChar;
			} else
				currentLine += currentChar;
		}
		
		pOccurrences.add(semicolonOccurrences);
		pOccurrences.add(openOccurrences);
		pOccurrences.add(closeOccurrences);
		return result;
	}
	
	public String getComments(String pSource) {
		return this.workaroundGetComments(pSource);
//		Pattern pattern = Pattern.compile("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)");
//		Matcher matcher = pattern.matcher(pSource);
//		
//		String comment = "";
//		while (matcher.find()) {
//			comment += matcher.group();
//		}
//		
//		return comment;
	}
	
	public String getMultilineComments(String pSource) {
		return this.workaroundGetMultilineComments(pSource);
//		Pattern pattern = Pattern.compile("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)");
//		Matcher matcher = pattern.matcher(pSource);
//		
//		String comment = "";
//		while (matcher.find()) {
//			comment += matcher.group();
//		}
//		
//		return comment;
	}
	
	@Override
	public String[] getIdentifiersFromSource(String pSource) {
		List<String> keywords = this.getKeywords();
		if (pSource.trim().equals(""))
			return new String[] {};
		
		String[] words = FilterManager.applyNonWordFilter(pSource.trim()).replaceAll("\n", "").split(" ");
		
		List<String> identifiersList = new ArrayList<String>();
		for (int i = 0; i < words.length; i++)
			if (!words[i].trim().equals("") && !keywords.contains(words[i]))
				identifiersList.add(words[i]);
		
		String[] identifiersArray = new String[identifiersList.size()];
		for (int i = 0; i < identifiersArray.length; i++)
			identifiersArray[i] = identifiersList.get(i);
		
		return identifiersArray;
	}
	
	public String[] getKeywordsFromSource(String pSource) {
		List<String> keywords = this.getKeywords();
		String[] words = FilterManager.applyNonWordFilter(pSource).replaceAll("\n", "").split(" ");
		
		List<String> keywordsList = new ArrayList<String>();
		for (int i = 0; i < words.length; i++)
			if (keywords.contains(words[i]))
				keywordsList.add(words[i]);
		
		String[] keywordsArray = new String[keywordsList.size()];
		for (int i = 0; i < keywordsArray.length; i++)
			keywordsArray[i] = keywordsList.get(i);
		
		return keywordsArray;
	}
	
	public String[] getIdentifierWords(String pIdentifier) {
		   return pIdentifier.replaceAll(
		      String.format("%s|%s|%s",
		         "(?<=[A-Z])(?=[A-Z][a-z])",
		         "(?<=[^A-Z])(?=[A-Z])",
		         "(?<=[A-Za-z])(?=[^A-Za-z])"
		      ),
		      " "
		   ).replaceAll("_", " ").replaceAll(" +(?= )","").replaceAll("\n", "").split(" ");
	}
	
	public List<String> getKeywords() {
		List<String> keywords = new ArrayList<String>();
		
		keywords.add("abstract");
		keywords.add("continue");
		keywords.add("for");
		keywords.add("new");
		keywords.add("switch");
		keywords.add("assert");
		keywords.add("default");
		keywords.add("if");
		keywords.add("package");
		keywords.add("synchronized");
		keywords.add("boolean");
		keywords.add("do");
		keywords.add("goto");
		keywords.add("private");
		keywords.add("this");
		keywords.add("break");
		keywords.add("double");
		keywords.add("implements");
		keywords.add("protected");
		keywords.add("throw");
		keywords.add("byte");
		keywords.add("else");
		keywords.add("import");
		keywords.add("public");
		keywords.add("throws");
		keywords.add("case");
		keywords.add("enum");
		keywords.add("instanceof");
		keywords.add("return");
		keywords.add("transient");
		keywords.add("catch");
		keywords.add("extends");
		keywords.add("int");
		keywords.add("short");
		keywords.add("try");
		keywords.add("char");
		keywords.add("final");
		keywords.add("interface");
		keywords.add("static");
		keywords.add("void");
		keywords.add("class");
		keywords.add("finally");
		keywords.add("long");
		keywords.add("strictfp");
		keywords.add("volatile");
		keywords.add("const");
		keywords.add("float");
		keywords.add("native");
		keywords.add("super");
		keywords.add("while");
		
		return keywords;
	}
	
	@Override
	public List<String> getLoopKeywords() {
		List<String> keywords = new ArrayList<String>();
		
		keywords.add("do");
		keywords.add("for");
		keywords.add("while");
		
		return keywords;
	}
	
	@Override
	public List<String> getConditionalKeywords() {
		List<String> keywords = new ArrayList<String>();
		
		keywords.add("if");
		
		return keywords;
	}
	
	private List<Integer[]> workaroundGetCommentsRanges(String pSource, boolean pMultilineOnly) {
		//State list:
		// 0: Normal code
		// 1: "/" found
		// 2: "/*" found (in comment)
		// 3: "*" found (maybe exit)
		int startComment = 0;
		int state = 0;
		
		List<Integer[]> ignores = new ArrayList<Integer[]>();
		for (int i = 0; i < pSource.length(); i++) {
			char current = pSource.charAt(i);
			switch (state) {
			case 0:
				if (current == '/')
					state = 1;
				
				break;
			case 1:
				if (current == '*') {
					state = 2;
					startComment = i - 1;
				} else if (!pMultilineOnly && current == '/') {
					state = 4;
					startComment = i - 1;
				} else
					state = 0;
				
				break;
			case 2:
				if (current == '*')
					state = 3;
				
				break;
			case 3:
				if (current == '/') {
					ignores.add(new Integer[] {startComment, i});
					state = 0;
				} else if (current != '*')
					state = 2;
				
				break;
			case 4:
				if (current == '\n') {
					ignores.add(new Integer[] {startComment, i});
					state = 0;
				}
				break;
			}
		}
		
		return ignores;
	}
	
	public String workaroundRemoveMultilineComments(String pSource) {
		List<Integer[]> ignores = this.workaroundGetCommentsRanges(pSource, true);
		if (ignores.size() == 0)
			return pSource;
		
		int currentIgnore = 0;
		Integer[] currentRange;
		String result = "";
		for (int i = 0; i < pSource.length(); i++) {
			if (currentIgnore == ignores.size())
				currentRange = new Integer[] {pSource.length(), pSource.length()+1};
			else
				currentRange = ignores.get(currentIgnore);
			int from = currentRange[0];
			int to = currentRange[1];
			
			if (i < from)
				result += pSource.charAt(i);
			else if (i == to)
				currentIgnore++;
		}
		
		return result;
	}
	
	public String workaroundRemoveComments(String pSource) {
		List<Integer[]> ignores = this.workaroundGetCommentsRanges(pSource, false);
		if (ignores.size() == 0)
			return pSource;
		
		int currentIgnore = 0;
		Integer[] currentRange;
		String result = "";
		for (int i = 0; i < pSource.length(); i++) {
			if (currentIgnore == ignores.size())
				currentRange = new Integer[] {pSource.length(), pSource.length()+1};
			else
				currentRange = ignores.get(currentIgnore);
			int from = currentRange[0];
			int to = currentRange[1];
			
			if (i < from)
				result += pSource.charAt(i);
			else if (i == to)
				currentIgnore++;
		}
		
		return result;
	}
	
	public String workaroundGetMultilineComments(String pSource) {
		List<Integer[]> consider = this.workaroundGetCommentsRanges(pSource, true);
		if (consider.size() == 0)
			return "";
		
		String result = "";
		for (Integer[] range : consider) {
			String substring = pSource.substring(range[0]+2, range[1]-1);
			result += substring + " ";
		}
		
		return result;
	}
	
	public String workaroundGetComments(String pSource) {
		List<Integer[]> consider = this.workaroundGetCommentsRanges(pSource, false);
		if (consider.size() == 0)
			return "";
		
		String result = "";
		for (Integer[] range : consider) {
			String substring = pSource.substring(range[0]+2, range[1]);
			result += substring + ". ";
		}
		
		return result;
	}
}
