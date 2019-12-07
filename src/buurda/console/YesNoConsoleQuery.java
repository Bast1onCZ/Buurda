package buurda.console;

import java.util.Scanner;

/**
 *
 * @author mirkl
 */
public class YesNoConsoleQuery {
    private static final Scanner scanner = new Scanner(System.in);
    private final String query;
    
    public YesNoConsoleQuery(String query) {
        this.query = query;
    }
    
    /**
     * Gets response from user - either yes or no
     * 
     * @return boolean 
     */
    public boolean getResponse() {
        while(true) {
            System.out.println(query + " (yes/no)");
            String response = scanner.nextLine();
            
            if(response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
                return true;
            } else if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("no")) {
                return false;
            }
            
            System.out.println("Please enter correct answer ...");
            try {
                Thread.sleep(300);
            } catch(InterruptedException ex) {}
        }
    }
}
