package com.example.kol_2_2023;

import client.ServerThread;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import server.Server;

import java.io.IOException;

public class HelloController {
    private Server server;
    private ServerThread serverThread;
    private GraphicsContext graphicsContext;
    @FXML
    private TextField portField;

    @FXML
    private TextField addressField;

    @FXML
    private Slider radiusSlider;

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    protected void onStartServerClicked() {
        String host = addressField.getText().isEmpty() ? "localhost" : addressField.getText();
        int port = portField.getText().isEmpty() ? 8080 : Integer.parseInt(portField.getText());
        server = new Server(port);
        try {
            server.listen();
            serverThread = new ServerThread(host, port);
            serverThread.setDots(dot -> {
                graphicsContext.setFill(dot.color());
                graphicsContext.fillOval(dot.centerX(), dot.centerY(), dot.radius(), dot.radius());
            });
            serverThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server started, on port: " + port);
    }

    @FXML
    protected void onConnectClicked() {
        String host = addressField.getText().isEmpty() ? "localhost" : addressField.getText();
        int port = portField.getText().isEmpty() ? 8080 : Integer.parseInt(portField.getText());
        try {
            serverThread = new ServerThread(host, port);
            serverThread.setDots(dot -> {
                graphicsContext.setFill(dot.color());
                graphicsContext.fillOval(dot.centerX(), dot.centerY(), dot.radius(), dot.radius());
            });
            serverThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("You have been connected to server on port: " + port);
    }
    @FXML
    protected void onMouseClicked(MouseEvent event){
        double radius = radiusSlider.getValue();
        double centerX = event.getX() - (radius/2);
        double centerY = event.getY() - (radius/2);
        serverThread.send(centerX, centerY, radius, colorPicker.getValue());
    }

    @FXML
    public void initialize(){
        graphicsContext = canvas.getGraphicsContext2D();
    }
}