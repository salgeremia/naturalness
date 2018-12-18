/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cleanjavacode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cleanjavacode.JavaCodeAnalyzer;
import cleanjavacode.FilterManager;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author Valentina Piantadosi
 */
public class CleanJavaCodeManager {
    
    JavaCodeAnalyzer jca = new JavaCodeAnalyzer();
    public CleanJavaCodeManager(){
        
    }
    
    public File[] getListFiles(File pFolder){
        File[] listOfFiles = pFolder.listFiles();
        for(int i = 0; i < listOfFiles.length; i++){
            if(listOfFiles[i].isFile()){
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()){
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return listOfFiles;
    }
    
//    public void getWordClassJava(File[] pListOfFiles, File pFolder) throws FileNotFoundException, IOException{
//        
//        String line;
//        
//        for(int i = 0; i < pListOfFiles.length; i++){
//            ArrayList<String> identifiersForFile = new ArrayList<String>();
//            BufferedReader br = new BufferedReader(new FileReader(pListOfFiles[i].toString()));
//            while ((line = br.readLine()) != null){
//                //System.out.println("\nline: " + line);
//                line = line.replaceAll("[a-zA-Z_][a-zA-Z_0-9]*\\s*\\(", " ");
//                /*System.out.println("CANCELLAZIONE METODO");
//                System.out.println("line: " + line);*/
//                line = line.replaceAll("[\\r\\n\\s]+", " ");
//                /*System.out.println("CANCELLAZIONE SPAZI");
//                System.out.println("line: " + line);
//                System.out.println("STAMPA KEYWORDS");*/
//                String[] keywords = jca.getKeywordsFromSource(line);
//                for(int j = 0; j < keywords.length; j++){
//                    //System.out.println(j + ": " + keywords[j]);
//                }
//                //System.out.println("STAMPA IDENTIFIERS");
//                String[] identifiers = jca.getIdentifiersFromSource(line);
//                for(int j = 0; j < identifiers.length; j++){
//                    //System.out.println(j + ": " + identifiers[j]);
//                    identifiersForFile.add(identifiers[j]);
//                
//                }
//            }
//            System.out.println(pListOfFiles[i].toString());
//            for(int j = 0; j < identifiersForFile.size(); j++){
//                System.out.println(identifiersForFile.get(j));
//            }
//            
//        }
//    }
    
    public ArrayList<String> getWordClassJava(String pMethod) throws FileNotFoundException, IOException {

        ArrayList<String> identifiersForFile = new ArrayList<String>();
//        System.out.println("\nmethod: " + pMethod);
        pMethod = pMethod.replaceAll("[a-zA-Z_][a-zA-Z_0-9]*\\s*\\(", " ");
//        System.out.println("CANCELLAZIONE METODO");
//        System.out.println("method: " + pMethod);
        pMethod = pMethod.replaceAll("[\\r\\n\\s]+", " ");
//        System.out.println("CANCELLAZIONE SPAZI");
//        System.out.println("method: " + pMethod);
//        System.out.println("STAMPA KEYWORDS");
        String[] keywords = jca.getKeywordsFromSource(pMethod);
        for (int j = 0; j < keywords.length; j++) {
            //System.out.println(j + ": " + keywords[j]);
        }
        
        //System.out.println("STAMPA IDENTIFIERS");
        String[] identifiers = jca.getIdentifiersFromSource(pMethod);
        for (int j = 0; j < identifiers.length; j++) {
            //System.out.println(j + ": " + identifiers[j]);
            identifiersForFile.add(identifiers[j]);
        }

        for (int j = 0; j < identifiersForFile.size(); j++) {
//            System.out.println(identifiersForFile.get(j));
        }
        
        return identifiersForFile;
    }
    
    
}
