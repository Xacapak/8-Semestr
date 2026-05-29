package com.semestr_8.ComputerSimulation;

import java.util.Scanner;

public class ComputerSimulation {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean exitProgram = false;
        String lineSeparator = "=".repeat(50);


        while (!exitProgram){
            System.out.println(lineSeparator);
            System.out.println("Компьютерное моделирование");
            System.out.println(lineSeparator);
            System.out.println("Выберите задание:");
            System.out.println("1 - Задание (1)");
            System.out.println("2 - Задание (2)");
            System.out.println("3 - Задание (3)");
            System.out.println("4 - Лабораторные работы");
            System.out.println("5 - ВЫХОД");
            System.out.println(lineSeparator);

            byte taskNumber;

            try {
                taskNumber = scanner.nextByte();
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Введите число от 1 до 5.");
                scanner.nextLine();
                continue;
            }

            switch (taskNumber){
                case 1 -> {
                    Task1 task1 = new Task1();
                    task1.run();
                }
                case 2 -> {
                    Task2 task2 = new Task2();
                    task2.run();


                }
                case 3 -> {
                    Task3 task3 = new Task3();
                    task3.run();
                }
                case 4 -> {
                    System.out.println("Запуск графического приложения");
                    launchJavaFX();
                }
                case 5 ->{
                    System.out.println("Выход из программы.");
                    exitProgram = true;
                    }
                default -> System.out.println("Некорректный номер задания!\n");
            }
        }

        scanner.close();

    }

    private static void launchJavaFX() {
        // Запускаем JavaFX приложение
        javafx.application.Application.launch(JavaFXLauncher.class);
    }

}