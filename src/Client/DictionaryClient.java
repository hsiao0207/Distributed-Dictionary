/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Client;

import src.Common.DictionaryMessage;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

public class DictionaryClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Gson gson = new Gson();
    private int sleepDuration;

    public void connect(String serverAddress, int serverPort) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public DictionaryMessage executeRequest(DictionaryMessage request) {
        try {
            
            request.setSleepDuration(this.sleepDuration);
            // object to JSON
            String jsonRequest = gson.toJson(request);
            // send request to server
            writer.println(jsonRequest);

            String jsonResponse = reader.readLine();
            // JSON to object
            if (jsonResponse != null) {
                return gson.fromJson(jsonResponse, DictionaryMessage.class);
            }
        } catch (IOException e) {
            System.err.println("Error sending request: " + e.getMessage());
        }
        return null;
    }

    public void close() throws IOException {
        if (socket != null) socket.close(); 
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public DictionaryMessage queryWord(String word) {
        DictionaryMessage request = new DictionaryMessage();
        request.setCommand("QUERY");
        request.setWord(word);
        return executeRequest(request);
    }

    public DictionaryMessage addWord(String word, java.util.List<String> meanings) {
        DictionaryMessage request = new DictionaryMessage();
        request.setCommand("ADD");
        request.setWord(word);
        request.setMeanings(meanings);
        return executeRequest(request);
    }

    public DictionaryMessage removeWord(String word) {
        DictionaryMessage request = new DictionaryMessage();
        request.setCommand("REMOVE");
        request.setWord(word);
        return executeRequest(request);
    }

    public DictionaryMessage addMeaning(String word, String newMeaning) {
        DictionaryMessage request = new DictionaryMessage();
        request.setCommand("ADD_MEANING");
        request.setWord(word);
        request.setNewMeaning(newMeaning);
        return executeRequest(request);
    }

    public DictionaryMessage updateMeaning(String word, String oldMeaning, String newMeaning) {
        DictionaryMessage request = new DictionaryMessage();
        request.setCommand("UPDATE");
        request.setWord(word);
        request.setOldMeaning(oldMeaning);
        request.setNewMeaning(newMeaning);
        return executeRequest(request);
    }


}
