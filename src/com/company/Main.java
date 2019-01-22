package com.company;

import com.company.neuralnet.*;
import java.sql.*;
import java.*;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.io.File;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//Comment from home

public class Main {
    public static void main(String[] args) {

        for(int i=1;i<2;i++){
            System.out.println("asd"+i);
        }
        //Запуск сети
        //NeuralNet.ResetWeights();
        //NeuralNet newnet=new NeuralNet();
        //NeuralNet.Train(newnet);
        //NeuralNet.Test(newnet);

        System.out.println("Ok");
    }
}
//Считывание из БД
      /*  Connection conn = null;
        String dbName = "NNDB";
        String serverip="192.168.2.227";
        String serverport="3306";
        String url = "jdbc:sqlserver://"+serverip+"\\Student"+";databaseName="+dbName+"";
        Statement stmt = null;
        ResultSet result = null;
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String databaseUserName = "Student";
        String databasePassword = "S123456s";
        try {

            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, databaseUserName, databasePassword);
            stmt = conn.createStatement();
            result = null;
            String pa,us;
            result = stmt.executeQuery("select * from ExpirementInputs ");

            while (result.next()) {
                us=result.getString("det1");
                System.out.println(us);
            }

            conn.close();
            System.out.println("Connection OK");
        } catch (Exception e) {
            e.printStackTrace();
        }*/