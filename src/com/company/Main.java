package com.company;

import com.company.neuralnet.*;

public class Main {
    public static void main(String[] args) {
        //Запуск сети
        //NeuralNet.ResetWeights();
        //NeuralNet newnet=new NeuralNet();
        //NeuralNet.Train(newnet);
        //NeuralNet.Test(newnet);
        //newnet.Test();
        NeuralNet nnet=new NeuralNet("NNetSettings.txt", 1,"Train");
        nnet.Train();
        nnet.Test();

        System.out.println("Ok");
    }
}
