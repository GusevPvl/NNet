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


        //Запуск по новому
        NeuralNet nnet=new NeuralNet("NNetSettings.txt", 1,"Tr");
        //nnet.Train();
        nnet.Test();
        //NNExperiments.startExp("Expirements\\ExpirementParams.txt");

        System.out.println("Ok");
    }
}
