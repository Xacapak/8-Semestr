package com.semestr_8.ComputerSimulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загружаем ваш FXML файл
        // Важно: FXML файл должен быть в той же папке, что и классы
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        // Настройка окна
        primaryStage.setTitle("Лабораторные работы");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
}
