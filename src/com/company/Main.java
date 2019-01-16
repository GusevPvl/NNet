package com.company;

import com.company.neuralnet.*;
import java.io.File;
//Comment from home

public class Main {

    public static void main(String[] args) {

        NeuralNet newnet=new NeuralNet();
        NeuralNet.Train(newnet);
        NeuralNet.Test(newnet);

        System.out.println("Ok");
    }
}

