package buurda;

import buurda.analyse.AnalysisResult;
import buurda.analyse.TestAnalyser;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Buurda {

    private static final String[] escapeCharacters = {".", "?", "(", ")"};
    private static final String formatting = "utf-8";
    private static final TestAnalyser analyser = new TestAnalyser(formatting, escapeCharacters);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        chooser.setFileFilter(filter);
        
        int selectFileResult = chooser.showOpenDialog(null);
        if(selectFileResult != JFileChooser.APPROVE_OPTION) {
            System.out.println("Nebyl zvolen soubor");
            return;
        }
        
        File file = chooser.getSelectedFile();
        AnalysisResult analysisResult =  analyser.analyse(file);
        String[] printedResult = analysisResult.print();
        
        for(String s : printedResult) {
            System.out.println(s);
        }
    }
}
