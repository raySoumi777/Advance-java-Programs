package chat_application;

import java.io.*;
import java.net.Socket;

public class peerToPeer {
    private String friend;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public peerToPeer(String friend, String ip, int port) {
        this.friend = friend;
        try {
            socket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    private void listenForMessages() {
        try {
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }
                System.out.println(friend + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
                socket.close();
                System.out.println(friend + "'s connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
