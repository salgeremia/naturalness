package cleanjavacode;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeAnalyzer {
	private static int implementor;
	
	public static void setImplementor(int pImplementor) {
		CodeAnalyzer.implementor = pImplementor;
	}
	
	public static CodeAnalyzer getInstance() {
		return CodeAnalyzer.getInstance(CodeAnalyzer.implementor);
	}
	
	public static CodeAnalyzer getInstance(int pImplementor) {
			return new JavaCodeAnalyzer();
	}
	
	public static String getSingleWordRegex(String pString) {
		return "[\n\\p{Z}\\p{S}\\p{P}]" + pString + "[\\p{Z}\\p{S}\\p{P}\n]";
	}
	
	public abstract String deleteComments(String pSource);
	public abstract String deleteInlineComments(String pSource);
	public abstract String deleteMultilineComments(String pSource);
	public abstract List<String> eachVisualLine(String pSource);
	public abstract List<String> getKeywords();
	public abstract List<String> getLoopKeywords();
	public abstract List<String> getConditionalKeywords();
	public abstract String[] getKeywordsFromSource(String pSource);
	public abstract String filterControlStructureWords(String pSource);
	
	public abstract List<String> eachStatementLine(String pSource);
	public abstract List<String> eachConstructLine(String pSource, List<Integer> pOccurrences);
	public abstract String[] getIdentifiersFromSource(String pSource);
	public List<String> eachConstructLine(String pSource) {
		return eachConstructLine(pSource, new ArrayList<Integer>());
	}
	
	public abstract String getComments(String pSource);
	public abstract String getMultilineComments(String pSource);
	
	public abstract String[] getIdentifierWords(String pIdentifier);
}
