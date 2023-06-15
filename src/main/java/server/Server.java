package server;

import com.example.kol_2_2023.Dot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import javafx.scene.paint.Color;

public class Server {
    private final ServerSocket serverSocket;
    private final Connection databaseConnection;

    private final int port;
    private final List<ClientThread> clients = new ArrayList<>();
    public Server(int port) {
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
            Class.forName("org.sqlite.JDBC");
            this.databaseConnection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            System.out.println("Connected to database");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException {
        Thread listenThread = new Thread(() -> {
            while(true) {
                try {
                    Socket newClienSocket = serverSocket.accept();
                    ClientThread thread = new ClientThread(newClienSocket, this);
                    clients.add(thread);
                    thread.start();
                    ArrayList<Dot> dots = getSavedDots();
                    for (Dot dot: dots) {
                        thread.sendMessageToUser(dot.centerX() + ";" + dot.centerY() + ";" + dot.radius() + ";" + dot.color().toString());
                    }
                    System.out.println("New client connected");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        listenThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (databaseConnection != null && !databaseConnection.isClosed()) {
                    PreparedStatement statement = databaseConnection.prepareStatement("DELETE FROM dot WHERE port = ?");
                    statement.setInt(1, port);
                    statement.executeUpdate();
                    databaseConnection.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void broadcast(String message) {
        saveDot(Dot.fromMessage(message));
        for (ClientThread client: clients) {
                client.sendMessageToUser(message);
        }
    }

    public void removeClient(ClientThread client){
        clients.remove(client);
    }

    public void saveDot(Dot dot){
        try {
            PreparedStatement statement = databaseConnection.prepareStatement("INSERT INTO dot (x, y, radius, color, port) VALUES (?, ?, ?, ?, ?)");
            statement.setDouble(1, dot.centerX());
            statement.setDouble(2, dot.centerY());
            statement.setDouble(3, dot.radius());
            statement.setString(4, dot.color().toString());
            statement.setInt(5, port);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Dot> getSavedDots(){
        try {
            PreparedStatement statement = databaseConnection.prepareStatement("SELECT * FROM dot WHERE port = ?");
            statement.setInt(1, port);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Dot> dots = new ArrayList<>();
            while (resultSet.next()) {
                dots.add(new Dot(resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("radius"), Color.valueOf(resultSet.getString("color"))));
            }
            return dots;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
