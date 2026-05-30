/**
 * Name: Hsiao, Po-Hung
 * Student ID: 1719722
 */
package src.Client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import src.Common.DictionaryMessage;

/**
 * ~ Dictionary Client GUI Skeleton ~
 * This is a basic GUI for the Dictionary Client.
 * You need to integrate this with your socket communication and
 * protocol implementation.
 *
 * @author Hsiao, Po-Hung
 * Student ID: 1719722
 */
public class DictionaryClientGUI extends JFrame {

    // GUI Components
    private JTextField wordField;
    private JTextArea meaningArea;
    private JTextField existingMeaningField;
    private JTextField newMeaningField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton addWordButton;
    private JButton removeWordButton;
    private JButton addMeaningButton;
    private JButton updateMeaningButton;
    private JLabel statusLabel;

    // Connection status
    private boolean isConnected = false;
    
    private DictionaryClient client;

    // constructor
    public DictionaryClientGUI(DictionaryClient client) {
        this.client = client;
        initializeGUI();
        setConnectionStatus(true);
    }

    private void initializeGUI() {
        setTitle("Dictionary Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panels
        add(createConnectionPanel(), BorderLayout.NORTH);
        add(createOperationsPanel(), BorderLayout.CENTER);
        add(createResultPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder("Connection Status"));

        statusLabel = new JLabel("Not Connected");
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Word Panel
        mainPanel.add(createSearchPanel());

        // Add Word Panel
        mainPanel.add(createAddWordPanel());

        // Remove Word Panel
        mainPanel.add(createRemoveWordPanel());

        // Update Operations Panel
        mainPanel.add(createUpdatePanel());

        return mainPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Search Word"));

        wordField = new JTextField();
        searchButton = new JButton("Search");

        panel.add(new JLabel("Word:"), BorderLayout.WEST);
        panel.add(wordField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchWord();
            }
        });

        return panel;
    }

    private JPanel createAddWordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Add New Word"));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField addWordField = new JTextField();
        meaningArea = new JTextArea(3, 20);
        meaningArea.setLineWrap(true);
        meaningArea.setWrapStyleWord(true);
        JScrollPane meaningScroll = new JScrollPane(meaningArea);

        addWordButton = new JButton("Add Word");

        inputPanel.add(new JLabel("Word:"));
        inputPanel.add(addWordField);
        inputPanel.add(new JLabel("Meaning(s):"));
        inputPanel.add(meaningScroll);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addWordButton);

        panel.add(inputPanel, BorderLayout.CENTER);

        addWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addWord(addWordField.getText(), meaningArea.getText());
            }
        });

        return panel;
    }

    private JPanel createRemoveWordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Remove Word"));

        JTextField removeWordField = new JTextField();
        removeWordButton = new JButton("Remove");

        panel.add(new JLabel("Word:"), BorderLayout.WEST);
        panel.add(removeWordField, BorderLayout.CENTER);
        panel.add(removeWordButton, BorderLayout.EAST);

        removeWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeWord(removeWordField.getText());
            }
        });

        return panel;
    }

    private JPanel createUpdatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Update Operations"));

        JPanel operationsPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField updateWordField = new JTextField();
        existingMeaningField = new JTextField();
        newMeaningField = new JTextField();

        addMeaningButton = new JButton("Add Meaning");
        updateMeaningButton = new JButton("Update Meaning");

        operationsPanel.add(new JLabel("Word:"));
        operationsPanel.add(updateWordField);
        operationsPanel.add(new JLabel("Existing Meaning:"));
        operationsPanel.add(existingMeaningField);
        operationsPanel.add(new JLabel("New Meaning:"));
        operationsPanel.add(newMeaningField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addMeaningButton);
        buttonPanel.add(updateMeaningButton);

        panel.add(operationsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMeaning(updateWordField.getText(), newMeaningField.getText());
            }
        });

        updateMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMeaning(updateWordField.getText(),
                        existingMeaningField.getText(),
                        newMeaningField.getText());
            }
        });

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Results"));

        resultArea = new JTextArea(8, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Methods to be implemented by you

    /**
     * Search for a word in the dictionary
     */
    private void searchWord() {
        String word = wordField.getText().trim();
        if (word.isEmpty()) {
            displayResult("Error: Please enter a word to search.");
            return;
        }

        DictionaryMessage response = client.queryWord(word);
        if (response != null) {
            if ("SUCCESS".equals(response.getStatus())) {
                
                displayResult( word + ": " + response.getAnsMeaning());
            } else {
                displayResult("Server Error: " + response.getMessage());
            }
        } else {
            setConnectionStatus(false);
            displayResult("Error: Communication with server failed.");
        }
    }

    /**
     * Add a new word with meanings to the dictionary
     */
    private void addWord(String word, String meanings) {
        if (word.trim().isEmpty() || meanings.trim().isEmpty()) {
            displayResult("Error: Both word and meaning(s) are required.");
            return;
        }

        String target = word.toLowerCase().trim();
        java.util.List<String> meaningsList = java.util.Arrays.asList(meanings.split(";"));
        DictionaryMessage response = client.addWord(target, meaningsList);
        if (response != null) {
            if ("SUCCESS".equals(response.getStatus())) {
                
                displayResult(response.getMessage());
            } else {
                displayResult("Server Error: " + response.getMessage());
            }
        } else {
            setConnectionStatus(false);
            displayResult("Error: Communication with server failed.");
        }
    }

    /**
     * Remove a word from the dictionary
     * You need to implement this
     */
    private void removeWord(String word) {
        if (word.trim().isEmpty()) {
            displayResult("Error: Please enter a word to remove.");
            return;
        }

        String target = word.toLowerCase().trim();
        DictionaryMessage response = client.removeWord(target);
        if (response != null) {
            if ("SUCCESS".equals(response.getStatus())) {
                
                displayResult(response.getMessage());
            } else {
                displayResult("Server Error: " + response.getMessage());
            }
        } else {
            setConnectionStatus(false);
            displayResult("Error: Communication with server failed.");
        }
    }

    /**
     * Add a new meaning to an existing word
     * You need to implement this
     */
    private void addMeaning(String word, String newMeaning) {
        if (word.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            displayResult("Error: Both word and new meaning are required.");
            return;
        }

        String target = word.toLowerCase().trim();
        String newMeaningTarget = newMeaning.trim();
        DictionaryMessage response = client.addMeaning(target, newMeaningTarget);
        if (response != null) {
            if ("SUCCESS".equals(response.getStatus())) {
                
                displayResult(response.getMessage());
            } else {
                displayResult("Server Error: " + response.getMessage());
            }
        } else {
            setConnectionStatus(false);
            displayResult("Error: Communication with server failed.");
        }
    }

    /**
     * Update an existing meaning of a word
     * You need to implement this
     */
    private void updateMeaning(String word, String existingMeaning, String newMeaning) {
        if (word.trim().isEmpty() || existingMeaning.trim().isEmpty() || newMeaning.trim().isEmpty()) {
            displayResult("Error: Word, existing meaning, and new meaning are all required.");
            return;
        }

        String target = word.toLowerCase().trim();
        String existingMeaningTarget = existingMeaning.trim();
        String newMeaningTarget = newMeaning.trim();
        DictionaryMessage response = client.updateMeaning(target,existingMeaningTarget, newMeaningTarget);
        if (response != null) {
            if ("SUCCESS".equals(response.getStatus())) {
                
                displayResult(response.getMessage());
            } else {
                displayResult("Server Error: " + response.getMessage());
            }
        } else {
            setConnectionStatus(false);
            displayResult("Error: Communication with server failed.");
        }
    }

    /**
     * Display result in the result area
     */
    private void displayResult(String result) {
        resultArea.append(java.time.LocalTime.now() + ": " + result + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    /**
     * Update connection status
     * You should call this method when connection status changes
     */
    public void setConnectionStatus(boolean connected) {
        this.isConnected = connected;
        if (connected) {
            statusLabel.setText("Connected");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("Not Connected");
            statusLabel.setForeground(Color.RED);
        }

        // Enable/disable buttons based on connection status
        searchButton.setEnabled(isConnected);
        addWordButton.setEnabled(isConnected);
        removeWordButton.setEnabled(isConnected);
        addMeaningButton.setEnabled(isConnected);
        updateMeaningButton.setEnabled(isConnected);
    }

    /**
     * Main method for testing GUI
     * You should modify this to include command line argument parsing
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port> <sleep-duration>");
            return;
        }

        String serverAddress = args[0];
        int serverPort;
        int sleepDuration;

        try {
            serverPort = Integer.parseInt(args[1]);
            sleepDuration = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Server port and sleep duration must be numbers.");
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                    DictionaryClient client = new DictionaryClient();
                    client.connect(serverAddress, serverPort);

                    client.setSleepDuration(sleepDuration);

                    DictionaryClientGUI gui = new DictionaryClientGUI(client);
                    gui.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Connection failed: " + e.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        });
    }
}