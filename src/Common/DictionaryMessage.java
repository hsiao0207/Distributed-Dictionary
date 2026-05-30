/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Common;
import java.util.List;

public class DictionaryMessage {
    // request message
    private String command; // QUERY, ADD, REMOVE, ADD_MEANING, UPDATE
    private String word; // FOR QUERY, ADD, REMOVE, ADD_MEANING, UPDATE
    private List<String> meanings; // FOR ADD
    private String oldMeaning; // FOR UPDATE
    private String newMeaning; // FOR ADD_MEANING, UPDATE
    private int sleepDuration; 
    // response message
    private String status; // SUCCESS, NOT_FOUND, DUPLICATE, ERROR
    private String message;
    private List<String> ansMeaning;

    // constructor
    public DictionaryMessage() {}
    
    public DictionaryMessage(String command, String word) {
        this.command = command;
        this.word = word;
    }

    //getters and setters
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public List<String> getMeanings() { return meanings; }
    public void setMeanings(List<String> meanings) { this.meanings = meanings; }

    public String getOldMeaning() { return oldMeaning; }
    public void setOldMeaning(String oldMeaning) { this.oldMeaning = oldMeaning; }

    public String getNewMeaning() { return newMeaning; }
    public void setNewMeaning(String newMeaning) { this.newMeaning = newMeaning; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String errorMessage) { this.message = errorMessage; }

    public List<String> getAnsMeaning() { return ansMeaning; }
    public void setAnsMeaning(List<String> ansMeaning) { this.ansMeaning = ansMeaning; }

    public int getSleepDuration() { return sleepDuration; }
    public void setSleepDuration(int sleepDuration) { this.sleepDuration = sleepDuration; }

}
