/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicspercentage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author Salvatore
 */
public class Analyzer {
    
    private static final String SRCML_PATH = "/usr/local/bin/srcml";
    private static final File DATA_DIR = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects");
    private static final File TOPICS_DIR = new File("/Users/Salvatore/Documents/UniMol/Dottorato/2018-2019/FSE/FSE_projects/api-list");
    
    public void start(File java_file){
        try {
            File temp_xml = new File(DATA_DIR + "/temp_class.xml");
            convert_to_xml(java_file, temp_xml);
            Document doc = Jsoup.parse(temp_xml, "UTF-8");
            Elements imports = doc.select("import > name");
            List<String> apis = merge_apis(TOPICS_DIR); 
            if (match(imports, apis)){ // Is there at least one api?
                Map<String, String> imports_map = new HashMap<>();
                List<String> apis_path = get_apis_path(imports, apis);
                for (String path : apis_path){
                    imports_map.put(path, get_type(path));   
                }
                Map<String, String> var_instance = get_var_instance(temp_xml);
                File methods_file = get_extractor_output(java_file);
                methods_extractor(temp_xml, methods_file);
                doc = Jsoup.parse(methods_file, "UTF-8");
                Elements methods = doc.getElementsByTag("unit");
                methods.remove(0);

                for (Element m : methods){
                    Map<String, String> parameters_map = get_parameters(m);
                    Map<String, String> method_var_map = get_method_var(m);
                    Element method_cleaned = preprocessing(m);
                    
                    /*
                    PARSING BY VALE
                    */
                    
                    /*
                    CALCOLA PERCENTUALI
                    */
                }
                
            } 
            
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void convert_to_xml(File input, File output){
        try {
            Process p = Runtime.getRuntime().exec(SRCML_PATH + " "
                    + input.getPath() + " -o " + output.getPath());
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void methods_extractor(File xml_file, File split_output){
        try {
            Process p = Runtime.getRuntime().exec(SRCML_PATH
                    + " --xpath /src:unit/src:class/src:block/src:function "
                    + xml_file.getPath() + " -o " + split_output.getPath());
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private File get_extractor_output(File java_file){
        File dir = new File(DATA_DIR.getPath() + "/snippets");
        if (!dir.exists()) {dir.mkdir();}
        String path = dir.getPath() + "/" + java_file.getParentFile().getName();
        dir = new File(path);
        if (!dir.exists()) {dir.mkdir();}
        path = dir.getPath() + "/" + java_file.getName().replace(".java", ".xml");
        return new File(path);
    }
    
    private List<String> merge_apis(File topics_dir) {
        List<String> apis = new ArrayList<>();
        for (File t : topics_dir.listFiles()) {
            if(!t.isHidden()){
               for (String a : get_apis(t)) {
                    apis.add(a);
                } 
            }
        }
        return apis;
    }
    
    private List<String> get_apis(File topic){
        List<String> apis = new ArrayList<>();
        try {
            apis = FileUtils.readLines(topic, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return apis;
    }
    
    private boolean match(Elements imports, List<String> apis) {       
        boolean match = false;
        for (Element i : imports) {
            match = apis.contains(i.text().replaceAll("[\\*;]", ""));
            if (match) break;
        }
        return match;
    }
    
    private List<String> get_apis_path(Elements imports, List<String> apis){ 
        List<String> path = new ArrayList<>();
        for (Element i : imports){
            for (String api : apis){
                if (api.contains(i.text().replaceAll("[\\*;]", ""))){
                    if (!path.contains(api)){
                        path.add(api);
                    }
                }
            }
        }
        return path;
    }
    
    private String get_type(String api_path) {
        api_path = api_path.replace(";", "");
        String[] path_split = api_path.split("\\.");
        return (path_split[path_split.length - 1]);
    }
    
    private Map<String, String> get_var_instance(File xml_file){
        Map<String, String> var_instance_map = new HashMap<>();
        try {
            Document doc = Jsoup.parse(xml_file, "UTF-8");
            Elements decl_stmt = doc.select("unit > class > block > decl_stmt");
            for (Element e : decl_stmt){              
                Element var_type_element = e.select("decl > type > name").first();
                String var_name = e.select("decl > type").first().nextElementSibling().text();  
                String var_type;
                if (var_type_element.children().isEmpty()){
                    var_type = var_type_element.text();
                } else {
                    var_type = var_type_element.child(0).text();
                }
                var_instance_map.put(var_name, var_type);
            }           
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return var_instance_map;
    }
      
    private Map<String, String> get_parameters(Element method){
        Map<String, String> parameters_map = new HashMap<>();
        Element decl = method.getElementsByTag("parameter_list").first();
        if (!decl.children().isEmpty()){
            Elements parameters = decl.select("parameter");
            for (Element p : parameters){
                Element type_elem = p.select("decl > type > name").first();
                String type_name;
                if (type_elem.children().isEmpty()){
                    type_name = type_elem.text();
                } else {
                    type_name = type_elem.select("name > name").first().text();
                }
                String var_name = p.select("decl > name").first().text();
                parameters_map.put(type_name, var_name);
            }
        }
        return parameters_map;
    }
    
    private Map<String, String> get_method_var(Element method){
        Map<String, String> method_map = new HashMap<>();
        Element block = method.getElementsByTag("block").first();
        Elements decl = block.getElementsByTag("decl_stmt");
        for (Element e : decl){
            Element var_type_element = e.select("decl > type > name").first();
            String var_name = e.select("decl > type").first().nextElementSibling().text();  
            String var_type;
            if (var_type_element.children().isEmpty()) {
                var_type = var_type_element.text();
            } else {
                var_type = var_type_element.child(0).text();
            }
            method_map.put(var_name, var_type);
        }
        return method_map;
    }
    
    private Element preprocessing(Element method){
        method.removeClass("comment");
        method.removeClass("literal");
        method.removeClass("type");
        return method;
    }
    
    public static void main(String[] args) throws IOException{        
        
    }
    
}
