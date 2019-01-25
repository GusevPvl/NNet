package com.company.neuralnet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class NeuralNet {
    //все слои сети, инициализация в конструкторах
    InputLayer input_layer;// = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
    public HiddenLayer hidden_layer;// = new HiddenLayer(200, input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden"); //Инициализация скрытого слоя
    //public HiddenLayer hidden_layer1 = new HiddenLayer(4, 4, NeuronType.hidden, "hidden1"); //Инициализация скрытого слоя
    public OutputLayer output_layer;// = new OutputLayer(input_layer.errorDB[1].length, 200, NeuronType.output, "output"); //Ининциализация выходного слоя

    public HiddenLayer[] hidden_layers;
    //массив для хранения выхода сети (фактические значения) - размерность: количество выходных нейронов
    public double[] fact;// = new double[input_layer.errorDB[1].length];

    //Стандартный конструктор
    public NeuralNet() {
        //все слои сети
        input_layer = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
        hidden_layer = new HiddenLayer(200, input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden"); //Инициализация скрытого слоя
        output_layer = new OutputLayer(input_layer.errorDB[1].length, 200, NeuronType.output, "output"); //Ининциализация выходного слоя
        fact = new double[input_layer.errorDB[1].length];//Инициализация массива фактических значений
    }

    //Конструктор для создания произвольного количества скрытых слоёв с заданным количеством нейронов
    public NeuralNet(String SettingsFile, int HiddenLayersNumber, String mode) {

        /*Path path = Paths.get("./big_file.txt");
        List<String> list1 = Files.readAllLines(path);*/

        input_layer = new InputLayer(); //Инициализация входного слоя - задается отдельным классом
        fact = new double[input_layer.errorDB[1].length];//Инициализация массива фактических значений
        hidden_layers = new HiddenLayer[HiddenLayersNumber]; //Инициализация массива скрытых слоев
        if (mode == "Train")
            WeightsFilesInitialize(SettingsFile, HiddenLayersNumber);
        File settings = new File(SettingsFile);
        try (FileReader reader = new FileReader(settings)) {

            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine(); //Считывание количества нейронов 1 скрытого слоя
            //Создание первого скрытого слоя, количество предыдущиъ нейронов - количество нейронов входного слоя
            hidden_layers[0] = new HiddenLayer(Integer.valueOf(line), input_layer.trainsetDB[1].length, NeuronType.hidden, "hidden");
            //Создание остальных скрытых слоев
            for (int i = 1; i < HiddenLayersNumber; i++) {
                line = br.readLine();
                hidden_layers[i] = new HiddenLayer(Integer.valueOf(line), hidden_layers[i - 1].numofneurons, NeuronType.hidden, "hidden" + i);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        output_layer = new OutputLayer(input_layer.errorDB[1].length, hidden_layers[hidden_layers.length - 1].numofneurons, NeuronType.output, "output"); //Ининциализация выходного слоя
    }

    //ошибка одной итерации обучения
    double GetMSE(double[] errors) {
        double sum = 0;
        for (int i = 0; i < errors.length; ++i)
            sum += Math.pow(errors[i], 2);
        return sum;
    }

    //ошибка эпохи
    double GetCost(double[] mses) {
        double sum = 0;
        for (int i = 0; i < mses.length; ++i)
            sum += mses[i];
        return (sum / mses.length);
    }

    //Обнуление весов
    public static void ResetWeights() {
        File hiddenfile = new File("hidden.txt");
        File outputfile = new File("output.txt");
        try (FileWriter writer = new FileWriter(hiddenfile, false)) {
            for (int l = 0; l < 16000; ++l) {
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        try (FileWriter writer = new FileWriter(outputfile, false)) {
            for (int l = 0; l < 800; ++l) {
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //непосредственно обучение
    public void Train()//backpropagation method
    {
        long startTrainTime = System.currentTimeMillis(); //Запись времени начала обчения
        final double threshold = 0.001;//порог ошибки
        double[] temp_mses = new double[input_layer.errorDB.length];//массив для хранения ошибок итераций
        double temp_cost = 0;//текущее значение ошибки по эпохе
        //Цикл до достижения указанного порога ошибки
        do {
            //Цикл по количеству "тренировочных" сетов
            for (int i = 0; i < input_layer.trainsetDB.length; ++i) {
                //прямой проход
                //Получение входных значений первым скрытым слоем (без фукции активации и без умножения на веса)
                hidden_layers[0].Data(input_layer.trainsetDB[i]);
                //Расчет с проходом по скрытым слоям
                for (int j = 1; j < hidden_layers.length; j++) {
                    hidden_layers[j - 1].OutputCalculate(this, hidden_layers[j]);
                }
                hidden_layers[hidden_layers.length - 1].OutputCalculate(this, output_layer);
                //Вычисление выходных значений сети (запись в net.fact)
                output_layer.OutputCalculate(this, null);
                //вычисление ошибки по итерации
                double[] errors = new double[input_layer.errorDB[i].length];
                for (int x = 0; x < errors.length; ++x)
                    errors[x] = input_layer.errorDB[i][x] - fact[x];
                temp_mses[i] = GetMSE(errors);
                //обратный проход и коррекция весов
                double[] temp_gsums = output_layer.BackwardPass(errors); //Распространение ошибки выходного слоя на входные веса
                //Коррекция весов по скрытым слоям
                //Как минимум 1 скрытый слой существует
                double[] temp_gsums_hidden = hidden_layers[hidden_layers.length - 1].BackwardPass(temp_gsums);
                if (hidden_layers.length > 1) {
                    for (int j = hidden_layers.length - 2; j >= 0; j--) {
                        temp_gsums_hidden = hidden_layers[j].BackwardPass(temp_gsums_hidden);
                    }
                }
            }
            temp_cost = GetCost(temp_mses);//вычисление ошибки по эпохе
            //debugging
            System.out.println(Double.toString(temp_cost));
            //WriteLine($"{temp_cost}");
        } while (temp_cost > threshold);
        long stopTrainTime = System.currentTimeMillis(); //Запись времени завершения обучения
        //Запись результатов обучения в файл
        WriteResultsToFile(MillisToHours(stopTrainTime - startTrainTime));
        //загрузка скорректированных весов в "память"
        //Запись весов скрытых слоев
        hidden_layers[0].WeightInitialize(MemoryMode.SET, "hidden");
        for(int i=1;i< hidden_layers.length;i++)
            hidden_layers[i].WeightInitialize(MemoryMode.SET, "hidden"+i);
        //Запись весов выходного слоя
        output_layer.WeightInitialize(MemoryMode.SET, "output");
    }

    //тестирование сети
    public void Test() {
        System.out.println("Результаты тестирования сети" + input_layer.trainset.length);
        for (int i = 0; i < input_layer.trainsetDB.length; ++i) {
            hidden_layers[0].Data(input_layer.trainsetDB[i]); //Как минимум 1 скрытый слой существует
            //Расчет с проходом по скрытым слоям
            for (int j = 1; j < hidden_layers.length; j++) {
                hidden_layers[j - 1].OutputCalculate(this, hidden_layers[j]);
            }
            hidden_layers[hidden_layers.length - 1].OutputCalculate(this, output_layer);
            output_layer.OutputCalculate(this, null);
            for (int j = 0; j < fact.length; ++j)
                System.out.print(fact[j] + " ");
            System.out.println();
        }
    }

    //Метод преобразования миллисекунд в ЧЧ:ММ:СС.ССС
    private static String MillisToHours(long millis) {
        String returntext;
        double resulttime = (double) (millis) / 1000;
        int hours = (int) resulttime / 3600;
        int minutes = (int) (resulttime % 3600) / 60;
        double seconds = (double) ((resulttime % 3600) % 60);
        java.text.DecimalFormat numberFormat = new java.text.DecimalFormat("#.000");
        returntext = hours + ":" + minutes + ":" + numberFormat.format(seconds) + millis;
        return returntext;
    }

    //Метод записи результатов обучения сети
    private static void WriteResultsToFile(String text) {
        File resultfile = new File("result.txt");
        try (FileWriter writer = new FileWriter(resultfile, false)) {
            writer.append("Время обучения НС: " + text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Метод создания файлов с весами для слоев сети
    private void WeightsFilesInitialize(String SettingsFile, int HiddenLayersNumber) {

        int prevLayerNeurons = 0;//Количество нейронов предыдущего слоя
        File settings = new File(SettingsFile);
        try (FileReader reader = new FileReader(settings)) {
            BufferedReader br = new BufferedReader(reader);
            String line;
            line = br.readLine(); //Считывание количества нейронов 1 скрытого слоя
            //Как минимум 1 скрытый слой всегда задается
            File hiddenfile = new File("hidden.txt");
            try (FileWriter writer = new FileWriter(hiddenfile, false)) {
                for (int l = 0; l < Integer.valueOf(line) * input_layer.trainsetDB[1].length; ++l) {
                    writer.append(Double.toString(0.0));
                    writer.append('\n');
                }
                writer.flush();
                prevLayerNeurons = Integer.valueOf(line);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            //Задание количества нейронов остальных скрытых слоёв
            for (int i = 1; i < HiddenLayersNumber; i++) {
                line = br.readLine();//Считывание количества нейронов остальных скрытых слоев
                hiddenfile = new File("hidden" + i + ".txt");
                try (FileWriter writer = new FileWriter(hiddenfile, false)) {
                    for (int l = 0; l < Integer.valueOf(line) * prevLayerNeurons; ++l) {
                        writer.append(Double.toString(0.0));
                        writer.append('\n');
                    }
                    writer.flush();
                    prevLayerNeurons = Integer.valueOf(line);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        //Создание файла для выходного слоя
        File outputfile = new File("output.txt");
        try (FileWriter writer = new FileWriter(outputfile, false)) {
            for (int l = 0; l < prevLayerNeurons * input_layer.errorDB[1].length; ++l) {
                writer.append(Double.toString(0.0));
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}

