package client;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import com.example.kol_2_2023.Dot;
import javafx.scene.paint.Color;
public class ServerThread extends Thread {
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private Consumer<Dot> dots;
    public ServerThread(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        OutputStream output = socket.getOutputStream();
        writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                dots.accept(Dot.fromMessage(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void send(double centerX, double centerY, double radius, Color myColor) {
        writer.println(centerX + ";" + centerY + ";" + radius + ";" + myColor.toString());
    }
    public void setDots(Consumer<Dot> dots) {
        this.dots = dots;
    }
}
