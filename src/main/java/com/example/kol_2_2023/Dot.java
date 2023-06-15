package com.example.kol_2_2023;

import javafx.scene.paint.Color;

public record Dot(double centerX, double centerY, double radius, Color color){
    public static Dot fromMessage(String messageFromServer){
        String [] parts = messageFromServer.split(";");
        double centerX = Double.parseDouble(parts[0]);
        double centerY = Double.parseDouble(parts[1]);
        double radius = Double.parseDouble(parts[2]);
        Color color = Color.valueOf(parts[3]);
        return new Dot(centerX, centerY, radius, color);
    }

}
