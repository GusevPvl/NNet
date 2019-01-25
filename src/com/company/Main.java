package com.company;

import com.company.neuralnet.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Main {
    public static void main(String[] args) {
        //Запуск сети
        //NeuralNet.ResetWeights();
        //NeuralNet newnet=new NeuralNet();
        //NeuralNet.Train(newnet);
        //NeuralNet.Test(newnet);
        //newnet.Test();


        List<String> list1;
        try  {
            Path path = Paths.get("hidden.txt");
            list1 = Files.readAllLines(path);
            for (int i=0;i<list1.size();i++)
            {
                System.out.println(list1.get(i));
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        //Запуск по новому
        //NeuralNet nnet=new NeuralNet("NNetSettings.txt", 1,"Tr");
        //nnet.Train();
        //nnet.Test();
        //NNExperiments.startExp("Expirements\\ExpirementParams.txt");

        System.out.println("Ok");
    }
}
