package chat_application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int SERVER_PORT = 12347;

    private Map<String, GUIConnection> guiConnections = new HashMap<>();

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is running on port " + SERVER_PORT);
            while (true) {
                Socket guiSocket = serverSocket.accept();
                GUIConnection guiConnection = new GUIConnection(guiSocket, this);
                guiConnection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void registerGUI(String guiName, GUIConnection guiConnection) {
        guiConnections.put(guiName, guiConnection);
        System.out.println("GUI registered: " + guiName);
    }

    public synchronized void broadcastMessage(String sender, String message) {
        for (GUIConnection guiConnection : guiConnections.values()) {
            guiConnection.sendMessage(sender, message);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}

