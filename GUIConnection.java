package chat_application;

import java.io.*;
import java.net.Socket;

public class GUIConnection extends Thread {
    private Socket guiSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;

    public GUIConnection(Socket guiSocket, Server server) {
        this.guiSocket = guiSocket;
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(guiSocket.getInputStream()));
            writer = new PrintWriter(guiSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String guiName = reader.readLine();
            server.registerGUI(guiName, this);

            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }
                server.broadcastMessage(guiName, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
                guiSocket.close();
                System.out.println("GUI connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String sender, String message) {
        writer.println(sender + ": " + message);
    }
}
