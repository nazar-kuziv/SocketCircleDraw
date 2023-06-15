package server;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread{
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Server server;
    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            InputStream input  = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            this.reader = new BufferedReader(new InputStreamReader(input));
            this.writer = new PrintWriter(output, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if(!message.isBlank()){
                    server.broadcast(message);
                }
            }
            server.removeClient(this);
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToUser(String message){
        writer.println(message);
    }
}
