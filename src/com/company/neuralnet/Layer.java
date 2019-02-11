package com.company.neuralnet;
//Comment from home

import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

abstract class Layer {//type используется для связи с одноимённым полю слоя файлом памяти

    public Layer() {
    }

    protected Layer(int non, int nopn, NeuronType nt, String type, boolean bias) {
        biasOn = bias;
        numofneurons = non;
        //Включение нейрона смещения. Фактически нейрон смещения не находится на предыдущем слое. Текущий слой его "подразумевает" и имеет связь с ним.
        if (biasOn)
            numofprevneurons = nopn + 1;
        else
            numofprevneurons = nopn;
        neurons = new Neuron[non];
        double[][] Weights = WeightInitialize(MemoryMode.GET, type);
        for (int i = 0; i < non; ++i) {
            double[] temp_weights = new double[numofprevneurons];
            for (int j = 0; j < numofprevneurons; ++j)
                temp_weights[j] = Weights[i][j];
            neurons[i] = new Neuron(null, temp_weights, nt);
        }
    }

    protected boolean biasOn;
    protected int numofneurons;//число нейронов текущего слоя
    protected int numofprevneurons;//число нейронов предыдущего слоя
    protected final double learningrate = 0.005d;//скорость обучения
    Neuron[] neurons;

    public Neuron[] getNeurons() {
        return neurons;
    }

    public void setNeurons(Neuron[] Neurons) {
        neurons = Neurons;
    }

    public void Data(double[] value) {
        if (biasOn) {
            double[] bias = new double[value.length + 1];
            for (int i = 0; i < value.length; i++) {
                bias[i] = value[i];
            }
            bias[bias.length - 1] = 1.0;

            for (int i = 0; i < neurons.length; i++)
                neurons[i].setInputs(bias);
        } else
            for (int i = 0; i < neurons.length; i++)
                neurons[i].setInputs(value);

    }

    public double[][] WeightInitialize(MemoryMode mm, String type) {
        double[][] weights = new double[numofneurons][numofprevneurons];//Нейрон смещения
        //System.out.println(type + " weights are being initialized...");
        File wfile = new File(type + ".txt");
        switch (mm) {
            case GET:
                try (FileReader reader = new FileReader(wfile)) {
                    BufferedReader br = new BufferedReader(reader);
                    String line;

                    for (int l = 0; l < weights.length; ++l)
                        for (int k = 0; k < weights[0].length; ++k) {
                            line = br.readLine();
                            weights[l][k] = Double.valueOf(line);
                        }

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                break;
            case SET:
                try (FileWriter writer = new FileWriter(wfile, false)) {
                    for (int l = 0; l < neurons.length; ++l)
                        for (int k = 0; k < numofprevneurons; ++k) {
                            writer.append(Double.toString(neurons[l].getWeights(k)));
                            writer.append('\n');
                        }
                    writer.flush();
                } catch (IOException ex) {

                    System.out.println(ex.getMessage());
                }
                break;
        }
        //System.out.println(type + " weights have been initialized...");
        return weights;
    }

    abstract public void OutputCalculate(NeuralNet net, Layer nextLayer);//для прямых проходов

    abstract public double[] BackwardPass(double[] stuff);//Проведение обратного распространения
}
