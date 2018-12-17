/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicspercentage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Salvatore
 */
public class TopicsPercentage {

    private static File[] getListFiles(File pFolder){
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Da aggiungere dopo
        File folder = new File("/Users/sciroppina/NetBeansProjects/naturalness/FSE_projects/java/prova/");
        File[] listFiles = getListFiles(folder);
        
        int id_method = 1;
        for(int i = 0; i < listFiles.length; i++){
            System.out.println(listFiles[i].getPath());
            File prova = new File(listFiles[i].getPath());
            Analyzer a = new Analyzer();
            id_method = a.start(prova, id_method);
            
        }    
        /*File prova = new File("/Users/sciroppina/NetBeansProjects/naturalness/FSE_projects/java/prova/A3CThreadDiscrete.java");
        Analyzer a = new Analyzer();
        a.start(prova);*/
    }
    
    
}
