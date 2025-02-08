/**
 * 
 */
package chat_application;

/**
 * 
 */
public class clientInfo {
    private String ip;
    private int port;

    public clientInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
