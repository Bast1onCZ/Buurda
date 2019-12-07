package buurda.analyse;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mirkl
 */
public class AnalysisResult {
    
    private static final String LINE_SEPARATOR = "----------------------------------------";
    protected final String fileName;
    protected int numberOfQuestionIndexes = -1;
    protected LinkedHashMap<Integer, Set<String>> questionsByQuestionIndex = null;
    
    public AnalysisResult(File analysedFile) {
        this.fileName = analysedFile.getName();
    }

    /**
     * Get the value of fileName
     *
     * @return the value of fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    public void setQuestionIndexes(Set<Integer> questionIndexes) {
        numberOfQuestionIndexes = questionIndexes.size();
        questionsByQuestionIndex = new LinkedHashMap<>(numberOfQuestionIndexes);
        for(int questionIndex : questionIndexes) {
            questionsByQuestionIndex.put(questionIndex, new LinkedHashSet<>());
        }
    }
    
    public Set<String> getIndexQuestions(int index) {
        return questionsByQuestionIndex.get(index);
    }
    
    public String[] print() {
        List<String> result = new ArrayList<>();
        result.add(LINE_SEPARATOR);
        result.add(String.format("Analysed test %s:", fileName));
        result.add(String.format("Test contains %s groups of questions", numberOfQuestionIndexes));
        result.add(LINE_SEPARATOR);
        result.add("");
        for(Map.Entry<Integer, Set<String>> group : questionsByQuestionIndex.entrySet()) {
            int groupIndex = group.getKey();
            String[] questionVariants = group.getValue().toArray(new String[group.getValue().size()]);
            
            result.add(LINE_SEPARATOR);
            result.add(String.format("%s. question has %s variants", groupIndex, questionVariants.length));
            result.add(LINE_SEPARATOR);
            for(int i = 0; i < questionVariants.length; i++) {
                result.add(String.format("%s) %s", i + 1, questionVariants[i]));
            }
            result.add("");
        }
        result.add(LINE_SEPARATOR);
        
        return result.toArray(new String[result.size()]);
    }

}
