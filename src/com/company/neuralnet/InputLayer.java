package com.company.neuralnet;

import java.io.*;
import java.lang.Math;

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
            "xor","xand","or"
    };

    public double[][] getTrainset() {
        return trainset;
    }
}
