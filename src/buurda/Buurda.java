package buurda;

import buurda.analyse.AnalysisResult;
import buurda.analyse.TestAnalyser;
import buurda.console.YesNoConsoleQuery;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Buurda {

    private static final String[] escapeCharacters = {".", "?", "(", ")"};
    private static final String formatting = "utf-8";
    private static final TestAnalyser analyser = new TestAnalyser(formatting, escapeCharacters);
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.removeChoosableFileFilter(chooser.getFileFilter()); // remove all files filter
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        
        int selectOpenFileResult = chooser.showOpenDialog(null);
        if(selectOpenFileResult != JFileChooser.APPROVE_OPTION) {
            System.out.println("No file was selected!");
            return;
        }
        
        File[] files = chooser.getSelectedFiles();
        AnalysisResult[] results = new AnalysisResult[files.length];
        for(int i = 0; i < files.length; i++) {
            AnalysisResult analysisResult =  analyser.analyse(files[i]);
            results[i] = analysisResult;
            
            String[] printedResult = analysisResult.print();
            for(String s : printedResult) {
                System.out.println(s);
            }
        }
        
        YesNoConsoleQuery shouldCreateFileQuery = new YesNoConsoleQuery("Would you like to create a file with results?");
        boolean shouldCreateFile = shouldCreateFileQuery.getResponse();
        
        if(shouldCreateFile) {
            chooser.setMultiSelectionEnabled(false);
            int selectSaveFileResult = chooser.showSaveDialog(chooser);
            if(selectSaveFileResult != JFileChooser.APPROVE_OPTION) {
                System.out.println("No file was selected!");
                return;
            }
            
            File fileToSave = chooser.getSelectedFile();
            if(!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() +".txt");
            }
            try(PrintWriter writer = new PrintWriter(fileToSave, formatting)) {
                for(AnalysisResult result : results) {
                    for(String line : result.print()) {
                        writer.println(line);
                    }
                }
            }
        }
    }
}
