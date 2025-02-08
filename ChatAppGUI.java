package chat_application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ChatAppGUI extends JFrame {
	private static final int SERVER_PORT = 12347;

    private JTextArea chatHistoryTextArea;
    private JList<String> friendsList;
    private JTextField nameField, ipField, portField;
    private JButton addButton, sendButton;

    private Map<String, clientInfo> friendsData = new HashMap<>();
    private Map<String, peerToPeer> activePeers = new HashMap<>();
    private Socket serverSocket;
    private PrintWriter serverWriter;

    public ChatAppGUI() {
        setTitle("Chat Application");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        addListeners();
        connectToServer();

        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Chat History
        chatHistoryTextArea = new JTextArea();
        chatHistoryTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatHistoryTextArea);
        chatScrollPane.setBorder(BorderFactory.createTitledBorder("Chat History"));
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Friends List
        friendsList = new JList<>();
        friendsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setBorder(BorderFactory.createTitledBorder("Friends List"));
        mainPanel.add(friendsScrollPane, BorderLayout.EAST);

        // User Input
        JPanel inputPanel = new JPanel(new GridLayout(1, 8));

        nameField = new JTextField();
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);

        ipField = new JTextField();
        inputPanel.add(new JLabel("IP Address:"));
        inputPanel.add(ipField);

        portField = new JTextField();
        inputPanel.add(new JLabel("port: "));
        inputPanel.add(portField);

        addButton = new JButton("Add");
        inputPanel.add(addButton);

        sendButton = new JButton("Send");
        inputPanel.add(sendButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String ip = ipField.getText();
                int port = Integer.parseInt(portField.getText());

                friendsData.put(name, new clientInfo(ip, port));
                updateFriendsList();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Simulate receiving messages in a separate thread (main method)
        new Thread(() -> {
            while (true) {
                // Simulate receiving a message (replace this with actual implementation)
               
                try {
                    Thread.sleep(5000); // Simulate a delay between messages
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
   

    private void connectToServer() {
        try {
            serverSocket = new Socket("localhost", SERVER_PORT);
            serverWriter = new PrintWriter(serverSocket.getOutputStream(), true);
            String guiName = JOptionPane.showInputDialog(this, "Enter your name:");
            serverWriter.println(guiName);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String[] selectedFriends = friendsList.getSelectedValuesList().toArray(new String[0]);

        if (selectedFriends.length == 0) {
            JOptionPane.showMessageDialog(this, "Select at least one friend to send the message.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = JOptionPane.showInputDialog(this, "Enter your message:");

        if (message != null && !message.isEmpty()) {
            for (String friend : selectedFriends) {
                serverWriter.println(friend + ": " + message);
            }
        }
    }

    private void updateFriendsList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String friend : friendsData.keySet()) {
            model.addElement(friend);
        }
        friendsList.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatAppGUI();
            }
        });
    }


    private void sendToFriend(String friend, String message) {
        if (activePeers.containsKey(friend)) {
            activePeers.get(friend).sendMessage(message);
        } else {
            clientInfo userInfo = friendsData.get(friend);
            if (userInfo != null) {
                peerToPeer peer = new peerToPeer(friend, userInfo.getIp(), userInfo.getPort());
                activePeers.put(friend, peer);
                peer.sendMessage(message);
            } else {
                JOptionPane.showMessageDialog(this, "Friend " + friend + " details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayMessage(String message) {
        chatHistoryTextArea.append(message + "\n");
    }

    
}
