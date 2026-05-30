/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Server;

import javax.swing.*;
import java.awt.*;

public class DictionaryServerGUI extends JFrame {
    private JLabel connectionLabel;
    private JTextArea logArea;
    private JButton startStopButton;
    private boolean isRunning = false;
    private DictionaryServer server;
    private int port;
    private String filePath;

    public DictionaryServerGUI(int port, String filePath) {
        this.port = port;
        this.filePath = filePath;
        initUI();
    }

    private void initUI() {
        setTitle("Dictionary Server Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top: status display and control button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionLabel = new JLabel("Active Connections: 0");
        startStopButton = new JButton("Start Server");
        topPanel.add(startStopButton);
        topPanel.add(connectionLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center: operation log
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Button behavior
        startStopButton.addActionListener(e -> toggleServer());
    }

    private void toggleServer() {
        if (!isRunning) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void startServer() {
        new Thread(() -> {
            try {
                server = new DictionaryServer(port, filePath, this);
                isRunning = true;
                SwingUtilities.invokeLater(() -> {
                    startStopButton.setText("Stop Server");
                    log("Server started on port " + port);
                });
                server.serverStart();
            } catch (Exception e) {
                log("Error: " + e.getMessage());
            }
        }).start();
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + new java.util.Date() + "] " + message + "\n");
        });
    }

    public void updateConnectionCount(int count) {
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("Active Connections: " + count);
        });
    }

    private void stopServer() {
        if (server != null) {
            server.stop(); 
        }
        
        isRunning = false; 
        startStopButton.setText("Start Server");
        log("Server stop command sent.");
        updateConnectionCount(0); 
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-path>");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            String path = args[1];

            SwingUtilities.invokeLater(() -> {
                DictionaryServerGUI gui = new DictionaryServerGUI(port, path);
                gui.setVisible(true);
            });   
        } catch (NumberFormatException e) {
            System.out.println("Port must be a number.");
        }
    }
}
