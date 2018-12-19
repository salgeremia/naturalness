/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topicspercentage;

import cleanjavacode.CleanJavaCodeManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
    private static final File DATA_DIR = new File("/Users/sciroppina/NetBeansProjects/naturalness/FSE_projects");
    private static final File TOPICS_DIR = new File("/Users/sciroppina/NetBeansProjects/naturalness/FSE_projects/api-list");
    private static final String[] TOPICS = {"collections", "files", "gui", "jdbc", "reflection", "servlet", "socket", "thread"};
    private static final String[] PRIMITIVE_DATA_TYPES = {"byte", "short", "int", "long", "float", "double", "char", "String", "boolean"};
    private Map<String, String> instance_var_map;
    private Map<String, String> parameters_map;
    private Map<String, String> method_var_map;
    
    public void start(File java_file) {
        try {
            File temp_xml = new File(DATA_DIR + "/temp_class.xml");
            convert_to_xml(java_file, temp_xml);
            Document doc = Jsoup.parse(temp_xml, "UTF-8");
            Elements imports = doc.select("import > name");
            List<String> apis = merge_apis(TOPICS_DIR);
            if (match(imports, apis)) { // Is there at least one api?
                Map<String, String> imports_map = new HashMap<>();
                List<String> apis_path = get_apis_path(imports, apis);
                for (String path : apis_path) {
                    imports_map.put(get_type(path), path);
                }
                instance_var_map = new HashMap<>();
                instance_var_map = get_var_instance(temp_xml);
                File methods_file = get_extractor_output(java_file);
                methods_extractor(temp_xml, methods_file);
                doc = Jsoup.parse(methods_file, "UTF-8");
                Elements methods = doc.getElementsByTag("unit");
                methods.remove(0);
                int unit = 0;

                for (Element m : methods) {
                    unit = unit + 1;
                    System.out.println("\nI'm into the method number " + unit);
                    parameters_map = new HashMap<>();
                    method_var_map = new HashMap<>();
                    parameters_map = get_parameters(m);
                    method_var_map = get_method_var(m);
                    Element method_cleaned = preprocessing(m.selectFirst("block"));

                    System.out.println("VAR INS: " + instance_var_map);
                    System.out.println("VAR PAR: " + parameters_map);
                    System.out.println("VAR LOC: " + method_var_map);

                    instance_var_map = remove_primitive_data_types(instance_var_map);
                    parameters_map = remove_primitive_data_types(parameters_map);
                    method_var_map = remove_primitive_data_types(method_var_map);

                    CleanJavaCodeManager cm = new CleanJavaCodeManager();
                    ArrayList<String> tokens = cm.getWordClassJava(method_cleaned.text());

                    System.out.println("tokens: " + tokens);

                    if (!tokens.isEmpty()) {
                        Map<String, Integer> token_count = token_counter(tokens, method_var_map);
//                        tokens = new ArrayList<>(token_count.keySet());
                        Map<String, String> token_topic = new HashMap<>();
                        for (String t : tokens) {
                            String candidate_type = null;

                            if (method_var_map.containsKey(t)) {
                                candidate_type = method_var_map.get(t);
//                                System.out.println(t + " is method var.");
                            } else if (parameters_map.containsKey(t)) {
                                candidate_type = parameters_map.get(t);
//                                    System.out.println(t + " is parameter var.");
                            } else if (instance_var_map.containsKey(t)) {
                                candidate_type = instance_var_map.get(t);
//                                    System.out.println(t + " is instance var.");
                            }

                            if (candidate_type == null) {
                                token_count.remove(t);
                            } else {
                                token_topic.put(t, get_topic(candidate_type, imports_map));
                            }
                        }
                        System.out.println("token_count " + token_count);
                        System.out.println("token_topic: " + token_topic);

                        int all_token = 0;
                        for (int i : token_count.values()) {
                            all_token = all_token + i;
                        }
                        int[] occurences = new int[TOPICS.length];
                        for (int index = 0; index < TOPICS.length; index++) {
                            System.out.print("topic -> " + TOPICS[index]);
                            if (token_topic.containsValue(TOPICS[index])) {
                                System.out.println(" -> SI");
                                List<String> keys = get_all_keys(token_topic, TOPICS[index]);
                                if (!keys.isEmpty()) {
                                    int topic_presence = 0;
                                    for (String k : keys) {
                                        topic_presence = topic_presence + token_count.get(k);
                                    }
                                    occurences[index] = topic_presence;
                                } else {
                                    occurences[index] = 0;
                                }
                            } else {
                                System.out.println(" -> NO");
                            }
                        }
                        int others;
                        if (sum(occurences) != 0) {
                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            others = all_token - sum(occurences);

                            File snippet = create_snippet(methods_file, unit);
                            int loc = get_loc(snippet);
                            File resultsPercentage = new File(DATA_DIR + "/resultsPercentage.txt");
                            File resultsOccurrences = new File(DATA_DIR + "/resultsOccurrences.txt");
                            String file_path = snippet.getParentFile().getName() + "_" + snippet.getName();
                            System.out.print(file_path + "  ");
                            for (int o = 0; o < occurences.length; o++) {
                                System.out.print(occurences[o] + "  ");
                            }
                            System.out.println(others + "  " + loc + "\t" + all_token);
                            System.out.print(file_path + "  ");
                            for (int o = 0; o < occurences.length; o++) {
                                System.out.print(((double) occurences[o] * 100 / all_token) + "  ");
                            }
                            System.out.print(((double) others * 100 / all_token) + "  " + loc + "\t" + all_token + "\n\n");

                            write_results(snippet, occurences, others, loc, all_token, resultsPercentage, resultsOccurrences);
                        }
                    } else {
                        System.out.println("...NO tokens, I'm sorry!");
                    }
                }

            }
            temp_xml.delete();
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    private double[] calculate_percentage(int[] topics_presence, int all_token){
//        double[] p = new double[topics_presence.length];
//        for(int i = 0; i < topics_presence.length; i++){
//            p[i] = topics_presence[i] * 100 / all_token;
//        }
//        return p;
//    }
    
    private void write_results(File snippet, int[] occurences, int others, int loc, int all_token, File output1, File output2){
        try {
            boolean header = false;
            boolean header1 = false;
            if (!output1.exists()){
                header = true;
            }
            if (!output2.exists()){
                header1 = true;
            }
            FileWriter fw = new FileWriter(output1.getAbsolutePath(), true);
            FileWriter fw1 = new FileWriter(output2.getAbsolutePath(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            if(header){
                bw.write("name,");
                for(String t : TOPICS){
                    bw.write(t + ",");
                }
                bw.write("other,loc");
            }
            if(header1){
            
                bw1.write("name,");
                for(String t : TOPICS){
            
                    bw1.write(t + ",");
                }
            
                bw1.write("other,loc");
            }
            String file_path = snippet.getParentFile().getName() + "_" + snippet.getName();
            bw.write("\n" + file_path + ",");
            bw1.write("\n" + file_path + ",");
            double value;
            for (int i = 0; i < occurences.length; i++){
                value = (double)occurences[i]*100/all_token;
                bw.write(value + ",");
                bw1.write(occurences[i] + ",");
            }
            value = (double)others*100/all_token;
            bw.write(value + "," + loc);
            bw1.write(others + "," + loc);
            bw.flush();
            bw1.flush();
            bw.close();
            bw1.close();
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File create_snippet(File methods_file, int unit) {
        File snippets_dir = new File(DATA_DIR + "/snippets");
        if (!snippets_dir.exists()) {
            snippets_dir.mkdir();
        }
        File project_dir = new File(snippets_dir + "/" + methods_file.getParentFile().getName());
        if (!project_dir.exists()) {
            project_dir.mkdir();
        }
        String output = project_dir.getPath() + "/" + 
                        FilenameUtils.removeExtension(methods_file.getName()) + 
                        "_" + unit + ".java";
        Process p;
        try {
            p = Runtime.getRuntime().exec(SRCML_PATH
                    + " --unit " + unit + " " + methods_file.getAbsolutePath() 
                    +  " -o " + output);
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new File(output);
    }

    private int sum(int[] v) {
        int s = 0;
        for (int i = 0; i < v.length; i++) {
            s = s + v[i];
        }
        return s;
    }

    private <k, v> List<k> get_all_keys(Map<k, v> mapOfWords, v value) {
        List<k> listOfKeys = null;

        if (mapOfWords.containsValue(value)) {
            listOfKeys = new ArrayList<>();
            for (Map.Entry<k, v> entry : mapOfWords.entrySet()) {
                if (entry.getValue().equals(value)) {
                    listOfKeys.add(entry.getKey());
                }
            }
        }
        return listOfKeys;
    }

    private List<File> get_topics() {
        List<File> topics = new ArrayList<>();
        for (File f : TOPICS_DIR.listFiles()) {
            if (!f.isHidden()) {
                topics.add(f);
            }
        }
        return topics;
    }

    private String get_topic(String type, Map<String, String> imports_map) {
        String topic = "other";
        if (imports_map.containsKey(type)) {
            String candidate_api = imports_map.get(type);
            for (File f : TOPICS_DIR.listFiles()) {
                if (!f.isHidden()) {
                    if (get_apis(f).contains(candidate_api)) {
                        topic = FilenameUtils.removeExtension(f.getName());
                        break;
                    }
                }
            }
        }
        return topic;
    }

    private Map<String, Integer> token_counter(ArrayList<String> tokens, Map<String, String> method_var_map) {
        Map<String, Integer> token_count = new HashMap<>();
        for (String s : tokens) {
            if (token_count.containsKey(s)) {
                int counter = token_count.get(s) + 1;
                token_count.replace(s, counter);
            } else if (method_var_map.containsKey(s)) {
                token_count.put(s, 0);
            } else {
                token_count.put(s, 1);
            }
        }
        return token_count;
    }

    private void convert_to_xml(File input, File output) {
        try {
            Process p = Runtime.getRuntime().exec(SRCML_PATH + " "
                    + input.getPath() + " -o " + output.getPath());
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void methods_extractor(File xml_file, File split_output) {
        try {
            Process p = Runtime.getRuntime().exec(SRCML_PATH
                    + " --xpath /src:unit/src:class/src:block/src:function "
                    + xml_file.getPath() + " -o " + split_output.getPath());
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File get_extractor_output(File java_file) {
        File dir = new File(DATA_DIR.getPath() + "/methods");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String path = dir.getPath() + "/" + java_file.getParentFile().getName();
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        path = dir.getPath() + "/" + java_file.getName().replace(".java", ".xml");
        return new File(path);
    }

    private List<String> merge_apis(File topics_dir) {
        List<String> apis = new ArrayList<>();
        for (File t : topics_dir.listFiles()) {
            if (!t.isHidden()) {
                for (String a : get_apis(t)) {
                    apis.add(a);
                }
            }
        }
        return apis;
    }

    private List<String> get_apis(File topic) {
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
            if (match) {
                break;
            }
        }
        return match;
    }

    private List<String> get_apis_path(Elements imports, List<String> apis) {
        List<String> path = new ArrayList<>();
        for (Element i : imports) {
            for (String api : apis) {
                if (api.contains(i.text().replaceAll("[\\*;]", ""))) {
                    if (!path.contains(api)) {
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

    private Map<String, String> get_var_instance(File xml_file) {
        Map<String, String> var_instance_map = new HashMap<>();
        try {
            Document doc = Jsoup.parse(xml_file, "UTF-8");
            Elements decl_stmt = doc.select("unit > class > block > decl_stmt");
            for (Element e : decl_stmt) {
                Element var_type_element = e.select("decl > type > name").first();
                String var_name = e.select("decl > type").first().nextElementSibling().text();
                String var_type;
                if (var_type_element.children().isEmpty()) {
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

    private Map<String, String> get_parameters(Element method) {
        System.out.println(method.text());
        Map<String, String> par_map = new HashMap<>();
        Element decl = method.getElementsByTag("parameter_list").first();
        if (!decl.attr("type").equals("generic")) {
            if (!decl.children().isEmpty()) {
                Elements parameters = decl.select("parameter");
                System.out.println(parameters.size());
                for (Element p : parameters) {
                    Element type_elem = p.select("decl > type > name").first();
                    String type_name;
                    if (type_elem.children().isEmpty()) {
                        type_name = type_elem.text();
                    } else {
                        type_name = type_elem.select("name > name").first().text();
                    }
                    String var_name = p.select("decl > name").first().text();
                    if (this.instance_var_map.containsKey(var_name)){
                        if (!this.instance_var_map.get(var_name).equals(type_name)){
                            System.err.println("\nTYPE CHANGE: " + var_name + " --- " + 
                                                this.instance_var_map.get(var_name) + " -> " + type_name);
                        }
                    }
                    par_map.put(var_name, type_name);
                }
            }
        }

//        par_map.forEach((type, name) -> System.out.println(type + " " + name));
        return par_map;
    }

    private Map<String, String> get_method_var(Element method) {
        Map<String, String> method_map = new HashMap<>();
        Element block = method.getElementsByTag("block").first();
        Elements decl = block.getElementsByTag("decl_stmt");
        for (Element e : decl) {
            Element var_type_element = e.select("decl > type > name").first();
            String var_name = e.select("decl > type").first().nextElementSibling().text();
            String var_type;
            if (var_type_element.children().isEmpty()) {
                var_type = var_type_element.text();
            } else {
                var_type = var_type_element.child(0).text();
            }
            if (this.parameters_map.containsKey(var_name)){
                if (!this.parameters_map.get(var_name).equals(var_type)){
                    System.err.println("\nTYPE CHANGE: " + var_name + " --- " + 
                                                this.parameters_map.get(var_name) + " -> " + var_type);
                }
            }
            if (this.instance_var_map.containsKey(var_name)){
                if (!this.instance_var_map.get(var_name).equals(var_type)){
                    System.err.println("\nTYPE CHANGE: " + var_name + " --- " + 
                                                this.instance_var_map.get(var_name) + " -> " + var_type);
                }
            }
            if (method_map.containsKey(var_name)){
                if (!method_map.get(var_name).equals(var_type)){
                    System.err.println("\nTYPE CHANGE: " + var_name + " --- " + 
                                        method_map.get(var_name) + " -> " + var_type);
                }
            }
            method_map.put(var_name, var_type);
        }
        return method_map;
    }

    private Element preprocessing(Element method) {
        method.select("comment").remove();
        method.select("literal").remove();
        method.select("type").remove();
        return method;
    }
    
    public int get_loc(File file) {
        BufferedReader reader;
        int lines = 0;
        try {
            reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            while (reader.readLine() != null) {
                lines++;
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines;
    }
    
    private Map<String, String> remove_primitive_data_types(Map<String, String> map_variable) {
        ArrayList<String> keyDeleted = new ArrayList<>();
        for (String key : map_variable.keySet()) {
            for (int i = 0; i < PRIMITIVE_DATA_TYPES.length; i++) {
                if (map_variable.get(key).equals(PRIMITIVE_DATA_TYPES[i])) {
                    keyDeleted.add(key);
                }
            }
        }
        for (int i = 0; i < keyDeleted.size(); i++) {
            map_variable.remove(keyDeleted.get(i));
        }
        return map_variable;
    }

    public static void main(String[] args) throws IOException {

    }

}
