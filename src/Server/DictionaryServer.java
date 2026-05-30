/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class DictionaryServer {
    private int port;
    private DictionaryManager dictionaryManager;
    private DictionaryServerGUI gui;
    private ServerSocket serverSocket;
    private volatile boolean isRunning = false;
    private AtomicInteger activeConnections = new AtomicInteger(0);

    // constructor
    public DictionaryServer(int port, String dictionaryPath, DictionaryServerGUI gui) {
        this.port = port;
        this.gui = gui;
        this.dictionaryManager = new DictionaryManager(dictionaryPath);
        this.dictionaryManager.loadDictionary(); 
        
    }
    
    public void serverStart() {
        isRunning = true;
        try {
            this.serverSocket = new ServerSocket(port);
            gui.log("Dictionary server listening on port: " + port);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
            
                int current = activeConnections.incrementAndGet();
                gui.updateConnectionCount(current);
                gui.log("New connection from: " + clientSocket.getInetAddress());

                ServerHandler handler = new ServerHandler(clientSocket, dictionaryManager, gui, this);
                Thread clientThread = new Thread(handler);
                clientThread.start(); 
            } 
        } catch (IOException e) {
            System.err.println("Server error occurred: " + e.getMessage());
        } finally {
            stop(); 
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientDisconnected() {
        int current = activeConnections.decrementAndGet();
        gui.updateConnectionCount(current);
    }
}
