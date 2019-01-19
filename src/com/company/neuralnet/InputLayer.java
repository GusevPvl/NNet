package com.company.neuralnet;


import java.sql.*;
import java.*;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.io.File;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

enum MemoryMode {GET, SET}

enum NeuronType {hidden, output}

class InputLayer {
    public double[][] trainset = new double[][]{
            {0, 0},
            {0, 1},
            {1, 0},
            {1, 1}
    };
    public double[][] error = new double[][]{
            {0, 1, 0},
            {1, 0, 1},
            {1, 0, 1},
            {0, 1, 1}
    };
    public String[] resultname = new String[]{
            "xor", "xand", "or"
    };

    //Тренировочный сет для загрузки из БД
    public double[][] transientDB;

    //Массив результатов для загрузки из БД
    public double[][] errorDB;

    //Конструктор с загрузкой данных из БД
    public InputLayer() {
        Connection conn = null; //Соединение с БД
        String dbName = "NNDB"; //Имя БД
        String serverip = "192.168.2.227"; //IP сервера
        String url = "jdbc:sqlserver://" + serverip + "\\Student" + ";databaseName=" + dbName + ""; //url-запрос на подключение
        Statement stmt = null; //Выражение для выполнения запроса
        ResultSet result = null; //Поле для выгрузки результатов запросов
        ResultSet result1 = null;
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //Используемый драйвер для подключения к MySQL
        String databaseUserName = "Student"; //Имя пользователя
        String databasePassword = "S123456s"; //Пароль пользователя
        try {
            Class.forName(driver).newInstance(); //Подключение драйвера
            conn = DriverManager.getConnection(url, databaseUserName, databasePassword); //Запрос соединения
            stmt = conn.createStatement(); //Создание выражения на текущем соединении
            //Запись trainsetDB
            result = null; //Обнуление поля результатов запроса
            result = stmt.executeQuery("select COUNT(*) from ExpirementInputs "); //Получение количества строк в таблице
            int rowsnum = 0; //Поле для записи количества строк
            if (result.next()) {
                rowsnum = result.getInt(1); //Получение количества строк
            }
            result = null; //Обнуление поля результатов запроса
            result = stmt.executeQuery("SELECT * FROM ExpirementInputs");
            int columnnum = 0; //Поле для получение количества столбцов для создания массива тренировочного сета
            columnnum = result.getMetaData().getColumnCount()-1; //Получение количества столбцов для создания массива тренировочного сета (9столбец - номер эксперимента)
            transientDB = new double[rowsnum][columnnum];//Создание тренировочного сета количество строк * количество столбцов
            //Считывание данных тренировочного сета
            while (result.next()) {
                for (int i = 0; i < columnnum; i++) {
                    transientDB[result.getRow()-1][i] = result.getInt(i+1); //Получение данных построчно (кроме 9 столбца)
                }
                System.out.println(result.getRow());
            }
            //Вывод trainsetDB для проверки
            for (int i=0;i<rowsnum;i++) {
                for (int j = 0; j < columnnum; j++)
                    System.out.print(transientDB[i][j]);
                System.out.println();
            }

            //Запись errorDB из БД
            result = null; //Обнуление поля результатов запроса
            result = stmt.executeQuery("select COUNT(*) from ExpPSRes "); //Получение количества строк в таблице
            if (result.next()) {
                rowsnum = result.getInt(1); //Получение количества строк в ExpPSRes
            }
            result = null; //Обнуление поля результатов запроса
            result = stmt.executeQuery("SELECT * FROM ExpPSRes");
            columnnum = result.getMetaData().getColumnCount()-1; //Получение количества столбцов для создания массива тренировочного сета ExpPSRes
            errorDB = new double[rowsnum][columnnum];//Создание тренировочного сета количество строк * количество столбцов
            //Считывание данных тренировочного сета
            while (result.next()) {
                for (int i = 0; i < columnnum; i++) {
                    errorDB[result.getRow()-1][i] = result.getDouble(i+1); //Получение данных построчно
                }
                System.out.println(result.getRow());
            }
            //Вывод trainsetDB для проверки
            for (int i=0;i<rowsnum;i++) {
                for (int j = 0; j < columnnum; j++)
                    System.out.print(errorDB[i][j]+" ");
                System.out.println();
            }
            /*String pa, us;
            result = stmt.executeQuery("select * from ExpirementInputs ");
            result1 = stmt.executeQuery("SELECT * FROM ExpirementInputs");
            int rowscol = 0;
            rowscol = result1.getMetaData().getColumnCount();
            System.out.println("Количество столбцов:" + rowscol);
            int count = 0;
            if (result1.next()) {
                count = result1.getInt(1);
            }
            System.out.println(count);


            conn.close();*/
            System.out.println("Connection OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public double[][] getTrainset() {
        return trainset;
    }
}
