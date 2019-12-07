package buurda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Buurda {

    private static final String[] escapeCharacters = {".", "?", "(", ")"};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.setFileFilter(filter);
        
        int result = chooser.showOpenDialog(null);
        if(result != JFileChooser.APPROVE_OPTION) {
            System.out.println("Nebyl zvolen soubor");
            return;
        }
        
        File file = chooser.getSelectedFile();
        System.out.println("Byl zvolen soubor "+ file.getName());
        
        List<String> currentIndexQuestions = null;
        String currentSection = "";
        
        Pattern questionIndexPattern = Pattern.compile("^(\\d+)\\.", Pattern.MULTILINE);
        Pattern startPattern = Pattern.compile("^(\\d+)\\. ");
        Pattern endPattern = Pattern.compile("\\d+ b$");
        
        String fileBuffer = "";
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"))) {
            String line;
            while((line = reader.readLine()) != null) {
                fileBuffer += line + '\n';
            }
        } catch(IOException ex) {
            System.out.println(ex);
            return;
        }
        
        Set<Integer> searchedQuestionIndexes = new LinkedHashSet<>();
        Matcher questionIndexMatcher = questionIndexPattern.matcher(fileBuffer);
        while(questionIndexMatcher.find()) {
            int questionIndex = Integer.parseInt(questionIndexMatcher.group(1));
            searchedQuestionIndexes.add(questionIndex);
        }
        
        HashMap<Integer, List<String>> questionsByTestIndex = new HashMap<>();
        searchedQuestionIndexes.forEach((index) -> {
            questionsByTestIndex.put(index, new ArrayList<>());
        });
        
        Reader baseReader = new InputStreamReader(new FileInputStream(file), "utf-8");
        try(BufferedReader reader = new BufferedReader(baseReader)) {
            String line;
            while((line = reader.readLine()) != null) {
                Matcher startMatcher = startPattern.matcher(line);
                if(startMatcher.find()) {
                    int index = Integer.parseInt(startMatcher.group(1));
                    currentIndexQuestions = questionsByTestIndex.get(index);
                    currentSection = "";
                }
                if(currentIndexQuestions != null) {
                    currentSection += line + " ";
                    
                    Matcher endMatcher = endPattern.matcher(line);
                    if(endMatcher.find()) {
                        if(!isContained(currentIndexQuestions, currentSection)) {
                            currentIndexQuestions.add(currentSection);
                        }
                    }
                }
            }
        } catch(Exception ex) {
            System.out.println("An error occured");
            System.out.print(ex);
        }
        
        System.out.println("Výsledky hledání:");
        searchedQuestionIndexes.forEach((index) -> {
            List<String> uniqueIndexQuestions = questionsByTestIndex.get(index);
            System.out.println("--------------------------------------------------------------------");
            System.out.println(index +". otázka - celkem "+ uniqueIndexQuestions.size() +" variant");
            System.out.println("--------------------------------------------------------------------");
            for(int i = 0; i < uniqueIndexQuestions.size(); i++) {
                System.out.println((i + 1) +") "+ uniqueIndexQuestions.get(i));
            }
        });
    }
    
    public static boolean isContained(List<String> in, String question) {
        String regexQuestion = replaceNumbersWithRegexPattern(escapeRegexCharacters(question));
        for(String q : in) {
            if(q.matches(regexQuestion)) {
                return true;
            }
        }
        return false;
    }
    
    public static String escapeRegexCharacters(String source) {
        String result = source;
        for(String escapeChar : escapeCharacters) {
            String sequence = "\\"+ escapeChar;
            result = result.replaceAll(sequence, "\\"+ sequence);
        }
        return result;
    }
    
    public static String replaceNumbersWithRegexPattern(String source) {
        String numberPattern = "-?\\d+(,\\d+)?";
        String replacePattern = "-?\\\\d+(,\\\\d+)?";
        
        return source.replaceAll(numberPattern, replacePattern);
    }
}
