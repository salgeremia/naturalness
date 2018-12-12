/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleanjavacode;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Valentina Piantadosi
 */
public class CleanJavaCode {

    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        CleanJavaCodeManager cm = new CleanJavaCodeManager();
        File folder = new File("/Users/sciroppina/Desktop/cleanCode/");
        //String method = "@Override public void incrementWordCount(String word, int increment) { T element = extendedVocabulary.get(word); if (element != null) { element.increaseElementFrequency(increment); totalWordCount.addAndGet(increment); } }";
        //String method = "@Override public int wordFrequency(@NonNull String word) { // TODO: proper wordFrequency impl should return long, instead of int T element = extendedVocabulary.get(word); if (element != null) return (int) element.getElementFrequency(); return 0; }";
        String method = "@Override public void addToken(T element) { T oldElement = vocabulary.putIfAbsent(element.getStorageId(), element); if (oldElement == null) { //putIfAbsent added our element if (element.getLabel() != null) { extendedVocabulary.put(element.getLabel(), element); } oldElement = element; } else { oldElement.incrementSequencesCount(element.getSequencesCount()); oldElement.increaseElementFrequency((int) element.getElementFrequency()); } totalWordCount.addAndGet((long) oldElement.getElementFrequency()); }";
        File[] listFiles = cm.getListFiles(folder);
        cm.getWordClassJava(method);
    }
    
}
