package buurda.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mirkl
 */
public class TestAnalyser {
    protected final String formatting;
    protected final String[] escapeCharacters;
    
    public TestAnalyser(String formatting, String[] escapeCharacters) {
        this.formatting = formatting;
        this.escapeCharacters = escapeCharacters;
    }
    
    public AnalysisResult analyse(File file) throws IOException {
        AnalysisResult result = new AnalysisResult(file);
        
        String currentSection = "";
        
        Pattern startPattern = Pattern.compile("^(\\d+)\\. ");
        Pattern endPattern = Pattern.compile("\\d+ b$");
        
        String[] fileLines = loadFileLines(file);
        Set<Integer> questionIndexes = analyseQuestionIndexes(fileLines);
        result.setQuestionIndexes(questionIndexes);
        
        Set<String> currentIndexQuestions = null;
        for(String line : fileLines) {
            Matcher startMatcher = startPattern.matcher(line);
            if(startMatcher.find()) {
                int index = Integer.parseInt(startMatcher.group(1));
                currentIndexQuestions = result.getIndexQuestions(index);
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
        
        return result;
    } 
    
    private String[] loadFileLines(File file) throws IOException {
        List<String> fileLines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), formatting)
        );
        // read whole file
        String line;
        while((line = reader.readLine()) != null) {
            fileLines.add(line);
        }
        
        return fileLines.toArray(new String[fileLines.size()]);
    }
    
    private Set<Integer> analyseQuestionIndexes(String[] fileLines) {
        Set<Integer> searchedQuestionIndexes = new LinkedHashSet<>();
        
        Pattern questionIndexPattern = Pattern.compile("^(\\d+)\\.", Pattern.MULTILINE);
        String fileString = String.join("\n", fileLines);
        Matcher questionIndexMatcher = questionIndexPattern.matcher(fileString);
        while(questionIndexMatcher.find()) {
            int questionIndex = Integer.parseInt(questionIndexMatcher.group(1));
            searchedQuestionIndexes.add(questionIndex);
        }
        
        return searchedQuestionIndexes;
    }
    
    private boolean isContained(Collection<String> in, String question) {
        String regexQuestion = replaceNumbersWithRegexPattern(escapeRegexCharacters(question));
        for(String q : in) {
            if(q.matches(regexQuestion)) {
                return true;
            }
        }
        return false;
    }
    
    private String escapeRegexCharacters(String source) {
        String result = source;
        for(String escapeChar : escapeCharacters) {
            String sequence = "\\"+ escapeChar;
            result = result.replaceAll(sequence, "\\"+ sequence);
        }
        return result;
    }
    
    private String replaceNumbersWithRegexPattern(String source) {
        String numberPattern = "-?\\d+(,\\d+)?";
        String replacePattern = "-?\\\\d+(,\\\\d+)?";
        
        return source.replaceAll(numberPattern, replacePattern);
    }
}
