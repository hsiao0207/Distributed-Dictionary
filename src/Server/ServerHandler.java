/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;
import src.Common.DictionaryMessage;

public class ServerHandler implements Runnable {
    private Socket clientSocket;
    private DictionaryManager dictionaryManager;
    private Gson gson = new Gson();
    private DictionaryServerGUI gui;
    private DictionaryServer server;

    // constructor
    public ServerHandler(Socket socket, DictionaryManager manager, DictionaryServerGUI gui, DictionaryServer server) {
        this.clientSocket = socket;
        this.dictionaryManager = manager;
        this.gui = gui;
        this.server = server;
    }

    // thread function
    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
                String clientRequest;
                while ((clientRequest = reader.readLine()) != null) {
                    // parse message(JSON -> Object)
                    DictionaryMessage requestMsg = gson.fromJson(clientRequest, DictionaryMessage.class);

                    //response message object
                    DictionaryMessage responseMsg = new DictionaryMessage();
                    String command = requestMsg.getCommand();
                    String word = requestMsg.getWord();
                    int delay = requestMsg.getSleepDuration();

                    switch (command) {
                        case "QUERY":
                            dictionaryManager.getLock().readLock().lock();
                            try {
                                simulateDelay(delay);
                                List<String> meanings = dictionaryManager.query(word);
                                if (meanings != null) {
                                    responseMsg.setStatus("SUCCESS");
                                    responseMsg.setAnsMeaning(meanings);
                                } else {
                                    responseMsg.setStatus("ERROR");
                                    responseMsg.setMessage("word not found");
                                }
                            } finally {
                                dictionaryManager.getLock().readLock().unlock();
                            }
                            break;

                         case "ADD":
                            dictionaryManager.getLock().writeLock().lock();
                            try {
                                simulateDelay(delay);
                                String addResult = dictionaryManager.add(word, requestMsg.getMeanings());
                                switch (addResult) {
                                    case "SUCCESS":
                                        responseMsg.setStatus("SUCCESS");
                                        responseMsg.setMessage("added successfully");
                                        break;
                                    case "DUPLICATE":
                                        responseMsg.setStatus("DUPLICATE");
                                        responseMsg.setMessage("word already exists");
                                        break;
                                    case "INVALID_MEANING":
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("meaning cannot be empty");
                                        break;
                                    default:
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("failed to add word");
                                        break;
                                }
                            } finally {
                                dictionaryManager.getLock().writeLock().unlock();
                            }
                            break;

                            case "REMOVE":
                            dictionaryManager.getLock().writeLock().lock();
                            try {
                                simulateDelay(delay);
                                String removeResult = dictionaryManager.remove(word);
                                switch (removeResult) {
                                    case "SUCCESS":
                                        responseMsg.setStatus("SUCCESS");
                                        responseMsg.setMessage("removed successfully");
                                        break;
                                    case "NOT_FOUND":
                                        responseMsg.setStatus("NOT_FOUND");
                                        responseMsg.setMessage("word not found");
                                        break;
                                    default:
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("failed to remove word");
                                        break;
                                }
                            } finally {
                                dictionaryManager.getLock().writeLock().unlock();
                            }
                            break;

                            case "ADD_MEANING":
                            dictionaryManager.getLock().writeLock().lock();
                            try {
                                simulateDelay(delay);
                                String amResult = dictionaryManager.addMeaning(word, requestMsg.getNewMeaning());
                                switch (amResult) {
                                    case "SUCCESS":
                                        responseMsg.setStatus("SUCCESS");
                                        responseMsg.setMessage("added meaning successfully");
                                        break;
                                    case "DUPLICATE":
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("meaning already exists");
                                        break;
                                    case "NOT_FOUND":
                                        responseMsg.setStatus("NOT_FOUND");
                                        responseMsg.setMessage("word not found");
                                        break;
                                    default:
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("failed to add meaning");
                                        break;
                                }
                            } finally {
                                dictionaryManager.getLock().writeLock().unlock();
                            }
                            break;

                            case "UPDATE":
                            dictionaryManager.getLock().writeLock().lock();
                            try {
                                simulateDelay(delay);
                                String upResult = dictionaryManager.update(word, requestMsg.getOldMeaning(), requestMsg.getNewMeaning());
                                switch (upResult) {
                                    case "SUCCESS":
                                        responseMsg.setStatus("SUCCESS");
                                        responseMsg.setMessage("updated meaning successfully");
                                        break;
                                    case "NOT_FOUND_OLD_MEANING":
                                        responseMsg.setStatus("NOT_FOUND");
                                        responseMsg.setMessage("existing meaning not found");
                                        break;
                                    case "NOT_FOUND":
                                        responseMsg.setStatus("NOT_FOUND");
                                        responseMsg.setMessage("word not found");
                                        break;
                                    default:
                                        responseMsg.setStatus("ERROR");
                                        responseMsg.setMessage("failed to update word");
                                        break;
                                }
                            } finally {
                                dictionaryManager.getLock().writeLock().unlock();
                            }
                            break;
                    }

                    // Return the response
                    String jsonResponse = gson.toJson(responseMsg);
                    writer.println(jsonResponse);
                }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            server.clientDisconnected(); 
            gui.log("Client disconnected: " + clientSocket.getInetAddress());
            try {
                clientSocket.close();
            } catch (IOException e) {}
        }
    }

    private void simulateDelay(int ms) {
        if (ms > 0) {
            try {
                System.out.println("Simulating operation delay: " + ms + "ms under lock protection.");
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                System.err.println("Delay interrupted: " + e.getMessage());
            }
        }
    }

}