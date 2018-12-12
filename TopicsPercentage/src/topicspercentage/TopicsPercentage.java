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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        File prova = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects/java/deeplearning4j-master/AbstractCache.java");
        Analyzer a = new Analyzer();
        a.start(prova);
    }
    
    
}
