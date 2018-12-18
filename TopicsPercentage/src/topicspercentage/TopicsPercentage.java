/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicspercentage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Salvatore
 */
public class TopicsPercentage {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
//        File prova = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects/java/deeplearning4j-master/AbstractCache.java");
//        File prova = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects/java/HikariCP-dev/ProxyFactory.java");
        Analyzer a = new Analyzer();
//        a.start(prova);
        
        File projects_dir = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects/java");
        for(File project : projects_dir.listFiles()){
            if(!project.isHidden()){
                for(File java_class : project.listFiles()){
                    if(!java_class.isHidden()){
                        a.start(java_class);
                    }
                }
            }
        }

    }
}
