package com.company;

import com.company.neuralnet.*;
import java.sql.*;
import java.*;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.io.File;

//Comment from home

public class Main {
    public static void main(String[] args) {

        Connection conn = null;
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
        }


        /*NeuralNet newnet=new NeuralNet();
        NeuralNet.Train(newnet);
        NeuralNet.Test(newnet);*/



        System.out.println("Ok");
    }
}

