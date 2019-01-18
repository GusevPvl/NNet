package com.company;

import com.company.neuralnet.*;
import java.sql.*;
import java.io.File;
//Comment from home

public class Main {

    public static void main(String[] args) {

        /*try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").   //db type
                    append("localhost:").      //host name
                    append("3306/").           //port
                    append("world").           //db name
                    append("user=root&").      //login
                    append("password=root");   //password

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString(), "root", "root");

        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            System.out.println("Error");
        }*/

        NeuralNet newnet=new NeuralNet();
        NeuralNet.Train(newnet);
        NeuralNet.Test(newnet);



        System.out.println("Ok");
    }
}

