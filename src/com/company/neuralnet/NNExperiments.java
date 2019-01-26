package com.company.neuralnet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class NNExperiments {
    public static void startExp(String paramsfile) {
        //HashMap для считывания параметров эксперимента
        Map<String, Integer> expirementparams = new HashMap<String, Integer>();
        //Коллекция для записи всех строк из файла
        List<String> ExpirementParamsFromFile;
        //Путь к файлу с параметрами
        Path path = Paths.get(paramsfile);
        try {
            //Считывание всех строк
            ExpirementParamsFromFile = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
            //Проход по всем строкам, разделение по :, запись в hashmap по ключу - значению
            for (int i = 0; i < ExpirementParamsFromFile.size(); i++) {
                String[] s = ExpirementParamsFromFile.get(i).split(":");
                expirementparams.put(s[0], Integer.valueOf(s[1]));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //Проход по всем параметрам эксперимента
        //По слоям
        for (int layersnum = expirementparams.get("minHiddenLayers"); layersnum < expirementparams.get("maxHiddenLayers") + 1; layersnum += expirementparams.get("stepHiddenLayers")) {
            //По нейронам
            for (int neuronsnum = expirementparams.get("minNeuronOnLayer"); neuronsnum < expirementparams.get("maxNeuronOnLayer") + 1; neuronsnum += expirementparams.get("stepNeuronOnLayer")) {
                //Создание файла с параметрами эксперимента
                File outputfile = new File("NNetSettings.txt");
                try (FileWriter writer = new FileWriter(outputfile, false)) {
                    //Цикл по количеству слоёв с записью нейронов на каждый слой
                    for (int i = 0; i < layersnum; i++) {
                        writer.append(Integer.toString(neuronsnum));
                        writer.append('\n');
                    }
                    writer.flush();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                //Запуск сети
                NeuralNet nnet=new NeuralNet("NNetSettings.txt","Train");
                nnet.Train();
            }
        }
    }
}
//Класс для хранения параметров экспериментов
class ExpParams {
    private int minHiddenLayers;
    private int maxHiddenLayers;
    private int minNeuronOnLayer;
    private int maxNeuronOnLayer;
    private int stepHiddenLayers;
    private int stepNeuronOnLayer;

    public void setMinHiddenLayers(int minHiddenLayers) {
        this.minHiddenLayers = minHiddenLayers;
    }

    public int getMinHiddenLayers() {
        return minHiddenLayers;
    }

    public void setMaxHiddenLayers(int maxHiddenLayers) {
        this.maxHiddenLayers = maxHiddenLayers;
    }

    public int getMaxHiddenLayers() {
        return maxHiddenLayers;
    }

    public void setMinNeuronOnLayer(int minNeuronOnLayer) {
        this.minNeuronOnLayer = minNeuronOnLayer;
    }

    public int getMinNeuronOnLayer() {
        return minNeuronOnLayer;
    }

    public void setMaxNeuronOnLayer(int maxNeuronOnLayer) {
        this.maxNeuronOnLayer = maxNeuronOnLayer;
    }

    public int getMaxNeuronOnLayer() {
        return maxNeuronOnLayer;
    }

    public void setStepHiddenLayers(int stepHiddenLayers) {
        this.stepHiddenLayers = stepHiddenLayers;
    }

    public int getStepHiddenLayers() {
        return stepHiddenLayers;
    }

    public void setStepNeuronOnLayer(int stepNeuronOnLayer) {
        this.stepNeuronOnLayer = stepNeuronOnLayer;
    }

    public int getStepNeuronOnLayer() {
        return stepNeuronOnLayer;
    }

}
